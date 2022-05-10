package zio.notion.model.user

import io.circe.generic.extras.ConfiguredJsonCodec

import java.util.UUID

@ConfiguredJsonCodec sealed trait User

object User {
  final case class Person(id: UUID, name: Option[String], avatarUrl: Option[String], person: PersonData) extends User
  final case class SimpleUser(id: UUID)                                                                  extends User
  final case class Bot(id: UUID, name: Option[String], avatarUrl: Option[String], bot: BotData)          extends User
  final case class Workspace(workspace: Boolean)                                                         extends User
}
