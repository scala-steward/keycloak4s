package com.fullfacing.keycloak4s.admin.monix.services

import java.nio.ByteBuffer

import com.fullfacing.keycloak4s.admin.monix.client.KeycloakClient
import com.fullfacing.keycloak4s.admin.services
import monix.eval.Task
import monix.reactive.Observable

class IdentityProviders(implicit client: KeycloakClient) extends services.IdentityProviders[Task, Observable[ByteBuffer]]
