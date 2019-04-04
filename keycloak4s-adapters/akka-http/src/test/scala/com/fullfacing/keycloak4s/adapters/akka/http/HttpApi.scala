package com.fullfacing.keycloak4s.adapters.akka.http

import akka.http.scaladsl.server.Directives._
import com.fullfacing.apollo.core.health.HealthCheck
import com.fullfacing.apollo.http.rest.BaseUri._
import com.fullfacing.keycloak4s.adapters.akka.http.apollo.BaseRoutesWithAuth
import com.fullfacing.keycloak4s.adapters.akka.http.apollo.BaseRoutesWithAuth.RequestHandler

object HttpApi extends BaseRoutesWithAuth("test" - "adaptor") {

  val resources: List[HealthCheck] = List.empty

  override val api: RequestHandler = { ctx =>
    get {
      complete(s"This is an auth test \n $ctx")
    }
  }
}
