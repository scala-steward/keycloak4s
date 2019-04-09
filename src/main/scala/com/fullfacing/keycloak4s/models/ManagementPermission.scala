package com.fullfacing.keycloak4s.models

final case class ManagementPermission(enabled: Option[Boolean],
                                      resource: Option[String],
                                      scopePermissions: Option[Map[String, String]])
