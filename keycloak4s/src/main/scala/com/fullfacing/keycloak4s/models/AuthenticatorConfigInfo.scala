package com.fullfacing.keycloak4s.models

final case class AuthenticatorConfigInfo(helpText: Option[String],
                                         name: Option[String],
                                         properties: Option[List[ConfigProperty]],
                                         providerId: Option[String])