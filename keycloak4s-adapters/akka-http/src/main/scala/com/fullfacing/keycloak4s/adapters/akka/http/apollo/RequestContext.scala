package com.fullfacing.keycloak4s.adapters.akka.http.apollo

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.model.RemoteAddress
import com.nimbusds.jose.Payload

/**
 * The request context class will be injected/forwarded at each program entry point. For example, all HTTP requests
 * will yield an implicit request context containing a correlationId generated by the API Gateway or Akka-Http. This
 * id is used to trace the request as it flows through the system.
 *
 * @param ip
 * @param agent
 * @param token         auth token used to authenticate and authorize the request
 * @param permissions   list of permissions in the user's token
 * @param timestamp     the timestamp that the original request entered the system.
 * @param correlationId a tracing id generated by Kong or akka-http.
 */
final case class RequestContext(ip: Option[RemoteAddress.IP] = None,
                                agent: Option[String] = None,
                                token: Option[String] = None,
                                permissions: Option[Payload] = None,
                                timestamp: Instant = Instant.now(),
                                correlationId: UUID = UUID.randomUUID())

object RequestContext {
  def SYSTEM: RequestContext = {
    RequestContext(
      timestamp = Instant.EPOCH,
      correlationId = UUID.randomUUID()
    )
  }
}