package zio.notion.model.user

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait Owner

object Owner {
  final case class Person(id: String, name: Option[String], avatarUrl: Option[String], person: Option[Person.Data]) extends Owner
  final case object Workspace                                                                                       extends Owner

  object Person {
    @ConfiguredJsonCodec final case class Data(email: String)
  }
}
