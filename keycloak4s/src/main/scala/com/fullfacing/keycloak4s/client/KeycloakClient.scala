package com.fullfacing.keycloak4s.client

import cats.effect.Concurrent
import com.fullfacing.keycloak4s.models.RequestInfo
import com.fullfacing.keycloak4s.models.enums.ContentTypes
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import com.softwaremill.sttp.json4s._
import com.softwaremill.sttp.{Id, Multipart, Request, RequestT, SttpBackend, Uri, sttp}
import org.json4s.Formats
import org.json4s.jackson.Serialization.read

import scala.collection.immutable.Seq
import scala.reflect._
import scala.reflect.runtime.universe.{TypeTag, typeTag}

class KeycloakClient[F[_] : Concurrent, -S](config: KeycloakConfig)(implicit client: SttpBackend[F, S], formats: Formats) extends TokenManager[F, S](config) {

  val realm: String = config.realm

  /* URI Builder **/
  private[client] def createUri(path: Seq[String], query: Seq[KeyValue]) = Uri(
    scheme         = config.scheme,
    userInfo       = None,
    host           = config.host,
    port           = Some(config.port),
    path           = Seq("auth", "admin", "realms") ++ path,
    queryFragments = query,
    fragment       = None
  )

  /* HTTP Call Builders **/

  private def setRequest[A](request: Request[String, Nothing], payload: A): RequestT[Id, String, Nothing] = payload match {
    case m: Map[_, _] => request.contentType(ContentTypes.UrlEncoded).body(m)
    case p: Multipart => request.contentType(ContentTypes.Multipart).body(p)
    case _: Unit      => request
    case j: AnyRef    => request.contentType(ContentTypes.Json).body(j)
  }

  private def setResponse[A <: Any : Manifest](request: RequestT[Id, String, Nothing])(implicit tag: TypeTag[A])
  : F[RequestT[Id, A, Nothing]] = tag match {
    case _ if tag == typeTag[Unit] => withAuth(request.mapResponse(_ => read[A]("null"))) //reading the string literal "null" is how you deserialize to a Unit with json4s
    case _                         => withAuth(request.response(asJson[A]))
  }

  private def call[A, B <: Any : Manifest](request: Request[String, Nothing], payload: A, requestInfo: RequestInfo): F[B] = {
    val resp = setResponse[B](setRequest(request, payload))
    F.flatMap(resp)(r => F.flatMap(r.send())(liftM(_, requestInfo)))
  }

  /* REST Protocol Calls **/

  def get[A <: Any : Manifest](path: Seq[String], query: Seq[KeyValue] = Seq.empty[KeyValue]): F[A] = {
    val request = sttp.get(createUri(path, query))
    call[Unit, A](request, (), buildRequestInfo(path, "GET", ()))
  }

  def put[A, B <: Any : Manifest](path: Seq[String], payload: A = (), query: Seq[KeyValue] = Seq.empty[KeyValue]): F[B] = {
    val request = sttp.put(createUri(path, query))
    call[A, B](request, payload, buildRequestInfo(path, "PUT", payload))
  }

  def post[A, B <: Any : Manifest](path: Seq[String], payload: A = (), query: Seq[KeyValue] = Seq.empty[KeyValue]): F[B] = {
    val request = sttp.post(createUri(path, query))
    call[A, B](request, payload, buildRequestInfo(path, "POST", payload))
  }

  def delete[A, B <: Any : Manifest](path: Seq[String], payload: A = (), query: Seq[KeyValue] = Seq.empty[KeyValue]): F[B] = {
    val request = sttp.delete(createUri(path, query))
    call[A, B](request, payload, buildRequestInfo(path, "DELETE", payload))
  }
}