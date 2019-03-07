package com.fullfacing.keycloak4s.services

import cats.effect.Effect
import com.fullfacing.keycloak4s.client.KeycloakClient
import com.fullfacing.keycloak4s.models._
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue

import scala.collection.immutable.Seq

class Clients[R[_]: Effect, S](implicit client: KeycloakClient[R, S]) {

  /**
   * Create a new initial access token.
   *
   * @param realm   Name of the Realm.
   * @param config
   * @return
   */
  def createNewInitialAccessToken(realm: String, config: ClientInitialAccessCreate): R[ClientInitialAccess] = {
    client.post[ClientInitialAccessCreate, ClientInitialAccess](config, realm :: "clients-initial-access" :: Nil)
  }

  /**
    * Retrieve all access tokens for the Realm.
    *
    * @param realm   Name of the Realm.
    * @return
    */
  def getInitialAccessTokens(realm: String): R[Seq[ClientInitialAccess]] = {
    client.get[Seq[ClientInitialAccess]](realm :: "clients-initial-access" :: Nil, Seq.empty[KeyValue])
  }

  /**
    * Delete an initial access token.
    *
    * @param realm   Name of the Realm.
    * @return
    */
  def deleteInitialAccessToken(tokenId: String, realm: String): R[Unit] = {
    client.delete(realm :: "clients-initial-access" :: tokenId :: Nil, Seq.empty[KeyValue])
  }


//    /**
//     * Create a new client.
//     * Client’s client_id must be unique!
//     *
//     * @param realm   Name of the Realm.
//     * @param client  Object representing a Client's details.
//     * @return
//     */
//    def createNewClient(realm: String, client: Client): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "clients")
//      client.post(client, path)
//    }

    /**
     * Returns a list of clients belonging to the realm
     *
     * @param realm         Name of the Realm.
     * @param clientId      Optional clientId filter.
     * @param viewableOnly  Optional filter for clients that cannot be viewed in full by admin.
     * @return
     */
    def getRealmClients(realm: String, clientId: Option[String] = None, viewableOnly: Boolean = false): R[Seq[Client]] = {
      val query = createQuery(("clientId", clientId), ("viewableOnly", Some(viewableOnly)))
      val path = Seq(realm, "clients")
      client.get(path, query)
    }

    /**
     * Get representation of a client.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def getClient(clientId: String, realm: String): R[Client] = {
      val path = Seq(realm, "clients", clientId)
      client.get[Client](path, Seq.empty[KeyValue])
    }

    /**
     * Update a client.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @param client    Object representing a Client's details.
     * @return
     */
    def updateClient(clientId: String, realm: String, c: Client): R[Unit] = { //TODO Determine return type.
      val path = Seq(realm, "clients", clientId)
      client.putNoContent(c, path, Seq.empty[KeyValue])
    }

    /**
     * Deletes a client.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def deleteClient(clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId)
      client.delete(path, Seq.empty[KeyValue])
    }

    /**
     * Generate a new secret for the client.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def generateClientSecret(clientId: String, realm: String): R[Credential] = {
      val path = Seq(realm, "clients", clientId, "client-secret")
      client.post[Credential](path)
    }

    /**
     * Get the client secret.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def getClientSecret(clientId: String, realm: String): R[Credential] = {
      val path = Seq(realm, "clients", clientId, "client-secret")
      client.get[Credential](path, Seq.empty[KeyValue])
    }

    /**
     * Get default client scopes.
     * Only name and ids are returned.
     *
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def getDefaultClientScopes(clientId: String, realm: String): R[List[ClientScope]] = {
      val path = Seq(realm, "clients", clientId, "default-client-scopes")
      client.get[List[ClientScope]](path, Seq.empty[KeyValue])
    }

    /**
     * ??? //TODO Determine route functionality.
     *
     * @param clientScopeId
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @return
     */
    def updateClientScope(clientScopeId: String, clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId, "default-client-scopes", clientScopeId)
      client.put(path, Seq.empty[KeyValue])
    }

    /**
     * Deletes a client scope.
     *
     * @param clientScopeId
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @return
     */
    def deleteClientScope(clientScopeId: String, clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId, "default-client-scopes", clientScopeId)
      client.delete(path, Seq.empty[KeyValue])
    }

    /**
     * Generate an example access token.
     *
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @param scope
     * @param userId
     * @return
     */
    def generateAccessTokenExample(clientId: String, realm: String, scope: Option[String] = None, userId: Option[String] = None): R[AccessToken] = {
      val query = createQuery(
        ("scope", scope),
        ("userId", userId)
      )

      val path = Seq(realm, "clients", clientId, "evaluate-scopes", "generate-example-access-token")
      client.get(path, query)
    }

    /**
     * Return list of all protocol mappers, which will be used when generating tokens issued for particular client.
     *
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @param scope
     * @return
     */
    def getProtocolMappers(clientId: String, realm: String, scope: Option[String] = None): R[Seq[ClientScopeEvaluateResourceProtocolMapperEvaluation]] = {
      val query = createQuery(("scope", scope))

      val path = Seq(realm, "clients", clientId, "evaluate-scopes", "protocol-mappers")
      client.get(path, query)
    }

    /**
     * Get effective scope mapping of all roles of particular role container, which this client is defacto allowed to have in the accessToken issued for him.
     * This contains scope mappings, which this client has directly, as well as scope mappings, which are granted to all client scopes, which are linked with this client.
     *
     * @param clientId        ID of client (not client-id).
     * @param realm           Name of the Realm.
     * @param roleContainerId Either realm name OR client UUID.
     * @param scope
     * @return
     */
    def getEffectiveScopeMapping(clientId: String, realm: String, roleContainerId: String, scope: Option[String]): R[Seq[Role]] = {
      val query = createQuery(("scope", scope))

      val path = Seq(realm, "clients", clientId, "evaluate-scopes", "scope-mappings", roleContainerId, "granted")
      client.get(path, query.to[Seq])
    }

    /**
     * Get roles, which this client doesn't have scope for and can't have them in the accessToken issued for him.
     *
     * @param clientId        ID of client (not client-id).
     * @param realm           Name of the Realm.
     * @param roleContainerId Either realm name OR client UUID.
     * @param scope
     * @return
     */
    def getNonScopeRoles(clientId: String, realm: String, roleContainerId: String, scope: Option[String]): R[Seq[Role]] = {
      val query = createQuery(("scope", scope))

      val path = Seq(realm, "clients", clientId, "evaluate-scopes", "scope-mappings", roleContainerId, "not-granted")
      client.get(path, query.to[Seq])
    }

//    /**
//     * Returns an installation provider.
//     *
//     * @param clientId    ID of client (not client-id).
//     * @param providerId  ID of provider.
//     * @param realm       Name of the Realm.
//     * @return
//     */
//    def getClientInstallationProvider(clientId: String, providerId: String, realm: String): R[Unit] = { //TODO Determine return type.
//      val path = Seq(realm, "clients", clientId, "installation", "providers", providerId)
//      client.ge(path)
//    }

    /**
     * Return object stating whether client Authorization permissions have been initialized or not and a reference.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def getClientAuthorizationPermissions(clientId: String, realm: String): R[ManagementPermission] = {
      val path = Seq(realm, "clients", clientId, "management", "permissions")
      client.get[ManagementPermission](path, Seq.empty[KeyValue])
    }

    /**
     * Update client Authorization permissions.
     *
     * @param clientId    ID of client (not client-id).
     * @param realm       Name of the Realm.
     * @param permission
     * @return
     */
    def updateClientAuthorizationPermissions(clientId: String, realm: String, permission: ManagementPermission): R[ManagementPermission] = {
      val path = Seq(realm, "clients", clientId, "management", "permissions")
      client.put[ManagementPermission, ManagementPermission](permission, path, Seq.empty[KeyValue])
    }

//    /**
//     * Register a cluster node with the client.
//     * Manually register cluster node to this client - usually it’s not needed to call this directly as adapter should handle by sending registration request to Keycloak.
//     *
//     * @param clientId    ID of client (not client-id).
//     * @param realm       Name of the Realm.
//     * @param formParams
//     * @return
//     */
//    def registerClusterNode(clientId: String, realm: String, formParams: Map[String, Any]): R[Unit] = { //TODO Determine formParams type.
//      val path = Seq(realm, "clients", clientId, "nodes")
//      client.post(formParams, path)
//    }

    /**
     * Unregister a cluster node from the client.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def unregisterClusterNode(clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId, "nodes")
      client.delete(path, Seq.empty[KeyValue])
    }

//    /**
//     * Get application offline session count.
//     * Returns a number of offline user sessions associated with this client { "count": number }.
//     *
//     * @param clientId ID of client (not client-id).
//     * @param realm    Name of the Realm.
//     * @return
//     */
//    def getOfflineSessionCount(clientId: String, realm: String): R[Map[String, Any]] = { //TODO Determine return type.
//      val path = Seq(realm, "clients", clientId, "offline-session-count")
//      client.get[Map[String, Any]](path, Seq.empty[KeyValue])
//    }

    /**
     * Get application offline sessions.
     * Returns a list of offline user sessions associated with this client.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @param first    Optional paging offset.
     * @param max      Optional maximum results size (defaults to 100).
     * @return
     */
    def getOfflineSessions(clientId: String, realm: String, first: Option[Int] = None, max: Option[Int] = None): R[List[UserSession]] = {
      val query = createQuery(
        ("first", first),
        ("max", max)
      )

      val path = Seq(realm, "clients", clientId, "offline-sessions")
      client.get[List[UserSession]](path, query)
    }

    /**
     * Returns optional client scopes.
     * Only name and ids are returned.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def getOptionalClientScopes(clientId: String, realm: String): R[List[ClientScope]] = {
      val path = Seq(realm, "clients", clientId, "optional-client-scopes")
      client.get[List[ClientScope]](path, Seq.empty[KeyValue])
    }

    /**
     * ??? //TODO Determine route functionality.
     *
     * @param clientScopeId
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @return
     */
    def updateOptionalClientScope(clientScopeId: String, clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId, "optional-client-scopes", clientScopeId)
      client.put(path, Seq.empty[KeyValue])
    }

    /**
     * Deletes an optional client scope.
     *
     * @param clientScopeId
     * @param clientId      ID of client (not client-id).
     * @param realm         Name of the Realm.
     * @return
     */
    def deleteOptionalClientScope(clientScopeId: String, clientId: String, realm: String): R[Unit] = {
      val path = Seq(realm, "clients", clientId, "optional-client-scopes", clientScopeId)
      client.delete(path, Seq.empty[KeyValue])
    }

    /**
     * Push the client’s revocation policy to its admin URL.
     * If the client has an admin URL, push revocation policy to it.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def pushRevocationPolicy(clientId: String, realm: String): R[GlobalRequestResult] = {
      val path = Seq(realm, "clients", clientId, "push-revocation")
      client.post[GlobalRequestResult](path)
    }

    /**
     * Generate a new registration access token for the client.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def generateRegistrationAccessToken(clientId: String, realm: String): R[Client] = {
      val path = Seq(realm, "clients", clientId, "registration-access-token")
      client.post[Client](path)
    }

    /**
     * Get a user dedicated to the service account.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def getServiceAccountUser(clientId: String, realm: String): R[User] = {
      val path = Seq(realm, "clients", clientId, "service-account-user")
      client.get[User](path, Seq.empty[KeyValue])
    }

    /**
     * Get application session count.
     * Returns a number of user sessions associated with this client { "count": number }.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def getSessionCount(clientId: String, realm: String): R[Map[String, Any]] = { //TODO Determine return type.
      val path = Seq(realm, "clients", clientId, "session-count")
      client.get[Map[String, Any]](path, Seq.empty[KeyValue])
    }

    /**
     * Test if registered cluster nodes are available.
     * Tests availability by sending 'ping' request to all cluster nodes.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @return
     */
    def testNodesAvailability(clientId: String, realm: String): R[GlobalRequestResult] = {
      val path = Seq(realm, "clients", clientId, "test-nodes-available")
      client.get[GlobalRequestResult](path)
    }

    /**
     * Get user sessions for client.
     * Returns a list of user sessions associated with this client.
     *
     * @param clientId ID of client (not client-id).
     * @param realm    Name of the Realm.
     * @param first    Optional paging offset.
     * @param max      Optional maximum results size (defaults to 100).
     * @return
     */
    def getUserSessions(clientId: String, realm: String, first: Option[Int] = None, max: Option[Int] = None): R[List[UserSession]] = {
      val query = createQuery(
        ("first", first),
        ("max", max)
      )

      val path = Seq(realm, "clients", clientId, "user-sessions")
      client.get[List[UserSession]](path, query)
    }

    /**
     * Base path for retrieving providers with the configProperties properly filled.
     *
     * @param realm Name of the Realm.
     * @return
     */
    def getClientRegistrationPolicyProviders(realm: String): R[List[ComponentType]] = {
      val path = Seq(realm, "client-registration-policy", "providers")
      client.get[List[ComponentType]](path)
    }


    //TODO Official documentation for ClientRoleMappings is lacking in detail and does not specify which "id" is required and if "client" is an ID or name.

    /**
     * Add client-level roles to the group role mapping.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @param roles
     * @return
     */
    def addRolesToGroup(name: String, id: String, realm: String, roles: Seq[Role]): R[Unit] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name)
      client.postNoContent(roles, path)
    }

    /**
     * Get client-level role mappings for the group.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getGroupRoleMappings(name: String, id: String, realm: String): R[Seq[Role]] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name)
      client.get(path)
    }

    /**
     * Delete client-level roles from group role mapping.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @param roles
     * @return
     */
    def deleteGroupRoles(name: String, id: String, realm: String, roles: Seq[Role]): R[Unit] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name)
      client.deleteNoContent(roles, path, Seq.empty[KeyValue])
    }

    /**
     * Get available client-level roles that can be mapped to the group.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getAvailableGroupRoles(name: String, id: String, realm: String): R[List[Role]] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name, "available")
      client.get[List[Role]](path)
    }

    /**
     * Get effective client-level group role mappings.
     * This recurses any composite roles.
     *
     * @param name
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getEffectiveGroupRoles(name: String, id: String, realm: String): R[List[Role]] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name, "composite")
      client.get[List[Role]](path)
    }

    /**
     * Add client-level roles to the user role mapping.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @param roles
     * @return
     */
    def addRolesToUser(name: String, id: String, realm: String, roles: Seq[Role]): R[Unit] = {
      val path = Seq(realm, "users", id, "role-mapping", "clients", name)
      client.postNoContent(roles, path)
    }

    /**
     * Get client-level role mappings for the user.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getUserRoleMappings(name: String, id: String, realm: String): R[List[Role]] = {
      val path = Seq(realm, "users", id, "role-mapping", "clients", name)
      client.get[List[Role]](path)
    }

    /**
     * Delete client-level roles from user role mapping.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @param roles
     * @return
     */
    def deleteUserRoles(name: String, id: String, realm: String, roles: Seq[Role]): R[Unit] = {
      val path = Seq(realm, "groups", id, "role-mapping", "clients", name)
      client.deleteNoContent(roles, path, Seq.empty[KeyValue])
    }

    /**
     * Get available client-level roles that can be mapped to the user.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getAvailableUserRoles(clientName: String, id: String, realm: String): R[List[Role]] = {
      val path = Seq(realm, "users", id, "role-mapping", "clients", clientName, "available")
      client.get[List[Role]](path)
    }

    /**
     * Get effective client-level user role mappings.
     * This recurses any composite roles.
     *
     * @param client
     * @param id
     * @param realm   Name of the Realm.
     * @return
     */
    def getEffectiveUserRoles(clientName: String, id: String, realm: String): R[List[Role]] = {
      val path = Seq(realm, "users", id, "role-mapping", "clients", clientName, "composite")
      client.get[List[Role]](path)
    }


    /**
     * Get key info.
     *
     * @param attribute
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def getKeyInfo(attribute: String, clientId: String, realm: String): R[Certificate] = {
      val path = Seq(realm, "clients", clientId, "certificates", attribute)
      client.get[Certificate](path)
    }

//    /**
//     * Get a keystore file for the client, containing private key and public certificate.
//     *
//     * @param attribute
//     * @param clientId  ID of client (not client-id).
//     * @param realm     Name of the Realm.
//     * @param config    Keystore configuration.
//     * @return
//     */
//    def getKeystoreFile(attribute: String, clientId: String, realm: String, config: KeyStoreConfig): R[File] = {
//      val path = Seq(realm, "clients", clientId, "certificates", attribute, "download")
//      client.post(config, path)
//    }

    /**
     * Generate a new certificate with new key pair
     *
     * @param attribute
     * @param clientId  ID of client (not client-id).
     * @param realm     Name of the Realm.
     * @return
     */
    def generateNewCertificate(attribute: String, clientId: String, realm: String): R[Certificate] = {
      val path = Seq(realm, "clients", clientId, "certificates", attribute, "generate")
      client.post[Certificate](path)
    }

//    /**
//     * Generates a keypair and certificate and serves the private key in a specified keystore format.
//     *
//     * @param attribute
//     * @param clientId  ID of client (not client-id).
//     * @param realm     Name of the Realm.
//     * @param config    Keystore configuration.
//     * @return
//     */
//    def generateAndDownloadNewCertificate(attribute: String, clientId: String, realm: String, config: KeyStoreConfig): R[File] = {
//      val path = Seq(realm, "clients", clientId, "certificates", attribute, "generate-and-download")
//      client.post(config, path)
//    }

//    /**
//     * Upload certificate and private key.
//     *
//     * @param attribute
//     * @param clientId    ID of client (not client-id).
//     * @param realm       Name of the Realm.
//     * @param file
//     * @param contentType The file's content type.
//     * @return
//     */
//    def uploadCertificateWithPrivateKey(attribute: String, clientId: String, realm: String, file: File, contentType: ContentType): R[Certificate] = {
//      val path = Seq(realm, "clients", clientId, "certificates", attribute, "upload")
//      val multipart = createMultipart(file, contentType)
//      client.post(multipart, path)
//    }

//    /**
//     * Upload only certificate, not private key.
//     *
//     * @param attribute
//     * @param clientId    ID of client (not client-id).
//     * @param realm       Name of the Realm.
//     * @param file
//     * @param contentType The file's content type.
//     * @return
//     */
//    def uploadCertificateWithoutPrivateKey(attribute: String, clientId: String, realm: String, file: File, contentType: ContentType): R[Certificate] = {
//      val path = Seq(realm, "clients", clientId, "certificates", attribute, "upload-certificate")
//      val multipart = createMultipart(file, contentType)
//      client.post(multipart, path)
//    }


//    /**
//     * Create a new client scope.
//     * Client Scope’s name must be unique!
//     *
//     * @param realm       Name of the Realm.
//     * @param clientScope Object representing ClientScope details.
//     * @return
//     */
//    def createNewClientScope(realm: String, clientScope: ClientScope): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes")
//      client.post(clientScope, path)
//    }

//    /**
//     * Update a client scope.
//     *
//     * @param scopeId     ID of the ClientScope.
//     * @param realm       Name of the Realm.
//     * @param clientScope Object representing ClientScope details.
//     * @return
//     */
//    def updateClientScope(scopeId: String, realm: String, clientScope: ClientScope): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes", scopeId)
//      client.put(clientScope, path)
//    }

//    /**
//     * Delete a client scope.
//     *
//     * @param scopeId ID of the ClientScope.
//     * @param realm   Name of the Realm.
//     * @return
//     */
//    def deleteClientScope(scopeId: String, realm: String): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes", scopeId)
//      client.delete(path)
//    }


//    /**
//     * Create a new client scope.
//     * Client Scope’s name must be unique!
//     *
//     * @param realm       Name of the Realm.
//     * @param clientScope Object representing ClientScope details.
//     * @return
//     */
//    def createNewClientScope(realm: String, clientScope: ClientScope): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes")
//      client.post(clientScope, path)
//    }

    /**
     * Returns a list of client scopes belonging to the realm.
     *
     * @param realm Name of the Realm.
     * @return
     */
    def getRealmClientScopes(realm: String): R[List[ClientScope]] = {
      val path = Seq(realm, "client-scopes")
      client.get[List[ClientScope]](path)
    }

    /**
     * Get representation of the client scope.
     *
     * @param scopeId ID of the ClientScope.
     * @param realm   Name of the Realm.
     * @return
     */
    def getClientScope(scopeId: String, realm: String): R[ClientScope] = {
      val path = Seq(realm, "client-scopes", scopeId)
      client.get(path)
    }

//    /**
//     * Update a client scope.
//     *
//     * @param scopeId     ID of the ClientScope.
//     * @param realm       Name of the Realm.
//     * @param clientScope Object representing ClientScope details.
//     * @return
//     */
//    def updateClientScope(scopeId: String, realm: String, clientScope: ClientScope): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes", scopeId)
//      client.put(clientScope, path)
//    }

//    /**
//     * Delete a client scope.
//     *
//     * @param scopeId ID of the ClientScope.
//     * @param realm   Name of the Realm.
//     * @return
//     */
//    def deleteClientScope(scopeId: String, realm: String): R[Any] = { //TODO Determine return type.
//      val path = Seq(realm, "client-scopes", scopeId)
//      client.delete(path)
//    }
}