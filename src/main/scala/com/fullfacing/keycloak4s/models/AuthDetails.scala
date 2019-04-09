package com.fullfacing.keycloak4s.models

final case class AuthDetails(clientId: Option[String],
                             ipAddress: Option[String],
                             realmId: Option[String],
                             userId: Option[String])