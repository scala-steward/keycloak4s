package com.fullfacing.keycloak4s.auth.akka.http

import com.fullfacing.keycloak4s.auth.akka.http.models.ResourceRoles
import com.nimbusds.jose.Payload

import scala.util.Try
import com.fullfacing.keycloak4s.core.serialization.JsonFormats.default
import org.json4s.Formats
import org.json4s.jackson.Serialization.read

/**
 * Provides helper functions to safely extract values from any Payload object.
 * Note: Any json4s parsing is *not* executed safely and will throw an exception in case of failure.
 */
object PayloadImplicits {

  /* A safe extraction method to extract any field's value from a Payload. **/
  private def safeExtract(payload: Payload, key: String): Option[String] = Try {
    payload.toJSONObject.getAsString(key)
  }.toOption

  implicit class PayloadImpl(payload: Payload) {

    /* Generic Extractors. **/

    def extract(key: String): Option[String] =
      safeExtract(payload, key)

    def extractList(key: String): List[String] =
      safeExtract(payload, key).map(read[List[String]](_)).getOrElse(List.empty[String])

    def extractAs[A : Manifest](key: String)(implicit formats: Formats = default): Option[A] =
      safeExtract(payload, key).map(read[A](_)(formats, manifest))

    def extractAsListOf[A : Manifest](key: String)(implicit formats: Formats = default): List[A] =
      safeExtract(payload, key).map(read[List[A]](_)(formats, manifest)).getOrElse(List.empty[A])

    /* User Info Extractors. **/

    def extractEmail: Option[String] =
      safeExtract(payload, "email")

    def extractEmailVerified: Option[Boolean] =
      extractAs[Boolean]("email_verified")

    def extractUsername: Option[String] =
      safeExtract(payload, "preferred_username")

    def extractFirstName: Option[String] =
      safeExtract(payload, "given_name")

    def extractSurname: Option[String] =
      safeExtract(payload, "family_name")

    def extractFullName: Option[String] =
      safeExtract(payload, "name")

    /* Access Control Extractors. **/

    def extractResourceAccess: Map[String, ResourceRoles] =
      extractAs[Map[String, ResourceRoles]]("resource_access").getOrElse(Map.empty[String, ResourceRoles])

    def extractResourceRoles(resource: String): List[String] =
      extractResourceAccess.get(resource).fold(List.empty[String])(_.roles)

    def extractRealmAccess: Option[ResourceRoles] =
      extractAs[ResourceRoles]("realm_access")

    def extractRealmRoles: List[String] =
      extractRealmAccess.fold(List.empty[String])(_.roles)

    def extractScopes: List[String] =
      extract("scope").fold(List.empty[String])(_.split(" ").toList)
  }
}
