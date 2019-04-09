package com.fullfacing.keycloak4s.models

final case class AuthenticatorConfig(alias: Option[String],
                                     config: Option[Map[String, String]],
                                     id: Option[String])
