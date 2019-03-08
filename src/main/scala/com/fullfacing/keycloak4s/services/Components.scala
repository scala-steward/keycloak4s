package com.fullfacing.keycloak4s.services

import cats.effect.Effect
import com.fullfacing.keycloak4s.client.KeycloakClient
import com.fullfacing.keycloak4s.models._
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue

import scala.collection.immutable.Seq

class Components[R[_]: Effect, S](implicit client: KeycloakClient[R, S]) {

  /**
   * Create a component.
   *
   * @param realm     Name of the Realm.
   * @param component Object representing a component's details.
   * @return
   */
  def createComponent(realm: String, component: Component): R[AnyRef] = { //TODO Determine return type.
    client.post[Component, AnyRef](component, realm :: "components" :: Nil)
  }

  /**
   * Retrieves all components for a Realm.
   *
   * @param realm Name of the Realm.
   * @return
   */
  def getComponents(realm: String): R[Seq[Component]] = {
    client.get[Seq[Component]](realm :: "components" :: Nil)
  }

  /**
   * Retrieves a component.
   *
   * @param componentId ID of the component.
   * @param realm       Name of the Realm.
   * @return
   */
  def getComponent(componentId: String, realm: String): R[Component] = {
    client.get[Component](realm :: "components" :: componentId :: Nil)
  }

  /**
   * Updates a component.
   *
   * @param componentId ID of the component.
   * @param realm       Name of the Realm.
   * @param component   Object representing a component's details.
   * @return
   */
  def updateComponent(componentId: String, realm: String, component: Component): R[Component] = {
    client.put[Component, Component](component, realm :: "components" :: componentId :: Nil, Seq.empty[KeyValue])
  }

  /**
   * Deletes a component.
   *
   * @param componentId ID of the component.
   * @param realm       Name of the Realm.
   * @return
   */
  def deleteComponent(componentId: String, realm: String): R[Unit] = {
    client.delete(realm :: "components" :: componentId :: Nil, Seq.empty[KeyValue])
  }

  /**
   * Retrieves list of subcomponent types that are available to configure for a particular parent component.
   *
   * @param componentId ID of the component.
   * @param realm       Name of the Realm.
   * @param `type`
   * @return
   */
  def getListOfSubComponentTypes(componentId: String, realm: String, `type`: Option[String] = None): R[Seq[ComponentType]] = {
    val query = createQuery(("type", `type`))
    client.get[Seq[ComponentType]](realm :: "components" :: componentId :: "sub-component-types", query)
  }
}
