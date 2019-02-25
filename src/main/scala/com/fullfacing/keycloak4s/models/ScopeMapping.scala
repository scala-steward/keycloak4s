package models

case class ScopeMapping(
                         client: Option[String],
                         clientScope: Option[String],
                         roles: Option[List[String]],
                         self: Option[String]
                       )
