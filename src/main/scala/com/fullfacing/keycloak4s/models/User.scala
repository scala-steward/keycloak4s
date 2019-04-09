package com.fullfacing.keycloak4s.models

final case class User(access: Option[UserAccess] = None,
                      attributes: Option[Map[String, String]] = None,
                      clientConsents: Option[List[UserConsent]] = None,
                      clientRoles: Option[Map[String, Any]] = None,
                      createdTimestamp: Option[Long] = None,
                      credentials: Option[List[Credential]] = None,
                      disableableCredentialTypes: Option[List[String]] = None,
                      email: Option[String] = None,
                      emailVerified: Option[Boolean] = None,
                      enabled: Option[Boolean] = None,
                      federatedIdentities: Option[List[FederatedIdentity]] = None,
                      federationLink: Option[String] = None,
                      firstName: Option[String] = None,
                      groups: Option[List[String]] = None,
                      id: Option[String] = None,
                      lastName: Option[String] = None,
                      notBefore: Option[String] = None,
                      origin: Option[String] = None,
                      realmRoles: Option[List[String]] = None,
                      requiredActions: Option[List[String]] = None,
                      self: Option[String] = None,
                      serviceAccountClientId: Option[String] = None,
                      username: Option[String])

final case class UserAccess(manage: Boolean,
                            impersonate: Boolean,
                            manageGroupMembership: Boolean,
                            mapRoles: Boolean,
                            view: Boolean)
