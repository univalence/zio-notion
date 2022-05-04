package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait User

object User {
  final case class Person(id: String, name: Option[String], avatarUrl: Option[String], person: PersonData) extends User
  final case class SimpleUser(id: String)                                                                  extends User
  final case class Bot(id: String, name: Option[String], avatarUrl: Option[String], bot: BotData)          extends User
  final case class Workspace(workspace: Boolean)                                                           extends User
}
