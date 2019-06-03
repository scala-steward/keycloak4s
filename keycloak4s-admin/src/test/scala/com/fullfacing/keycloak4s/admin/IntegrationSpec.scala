package com.fullfacing.keycloak4s.admin

import cats.effect.{ContextShift, IO}
import com.fullfacing.keycloak4s.admin.client.{Keycloak, KeycloakClient}
import com.fullfacing.keycloak4s.admin.services.{Clients, Groups, IdentityProviders, RealmsAdmin, Roles, RolesById, Users}
import com.fullfacing.keycloak4s.core.models.KeycloakConfig
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.scalatest._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class IntegrationSpec extends AsyncFlatSpec with Matchers with Inspectors with Inside {

  /* Keycloak Server Configuration **/
  val authConfig      = KeycloakConfig.Auth("master", "admin-cli", "e8fa2dd0-8775-43aa-af8a-e2eb157fa02f")//ServerInitializer.clientSecret)
  val keycloakConfig  = KeycloakConfig("http", "127.0.0.1", 8088, "Demo", authConfig)

  /* Keycloak Client Implicits **/
  implicit val context: ContextShift[IO]            = IO.contextShift(global)
  implicit val backend: SttpBackend[IO, Nothing]    = new IoHttpBackend(AsyncHttpClientCatsBackend[IO]())
  implicit val client: KeycloakClient[IO, Nothing]  = new KeycloakClient[IO, Nothing](keycloakConfig)

  /* Keycloak Services **/
  val userService: Users[IO, Nothing]               = Keycloak.Users[IO, Nothing]
  val roleService: Roles[IO, Nothing]               = Keycloak.Roles[IO, Nothing]
  val realmService: RealmsAdmin[IO, Nothing]        = Keycloak.RealmsAdmin[IO, Nothing]
  val groupService: Groups[IO, Nothing]             = Keycloak.Groups[IO, Nothing]
  val clientService: Clients[IO, Nothing]           = Keycloak.Clients[IO, Nothing]
  val idProvService: IdentityProviders[IO, Nothing] = Keycloak.IdentityProviders[IO, Nothing]
  val rolesByIdService: RolesById[IO, Nothing]      = Keycloak.RolesById[IO, Nothing]

  /* Sub-Services **/
  val realmRoleService: roleService.RealmLevel.type   = roleService.RealmLevel
  val clientRoleService: roleService.ClientLevel.type = roleService.ClientLevel

  /* Implicit Helper Classes **/
  implicit class ioImpl[A, B](io: IO[Either[B, A]]) {
    def shouldReturnSuccess: Future[Assertion] = io.map { response =>
      response shouldBe a [scala.util.Right[_, _]]
    }.unsafeToFuture()

    def shouldReturnError: Future[Assertion] = io.map { response =>
      response shouldBe a [scala.util.Left[_, _]]
    }.unsafeToFuture()
  }

  implicit class optImpl[A](opt: Option[A]) {
    def getWithAssert: A = {
      opt shouldBe defined
      opt.get
    }
  }

  implicit class seqImp[A](seq: Seq[A]) {
    def headWithAssert: A = {
      seq shouldNot be (empty)
      seq.head
    }
  }
}