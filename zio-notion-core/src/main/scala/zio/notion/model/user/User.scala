package zio.notion.model.user

import io.circe.{Decoder, HCursor}
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait User

object User {
  final case class Person(id: String, name: Option[String], avatarUrl: Option[String], person: PersonData) extends User
  final case class SimpleUser(id: String)                                                                  extends User
  final case class Bot(id: String, name: Option[String], avatarUrl: Option[String], bot: Option[BotData])  extends User
  final case class Workspace(workspace: Boolean)                                                           extends User

  @ConfiguredJsonCodec final case class BotData(owner: User)
  @ConfiguredJsonCodec final case class PersonData(email: Option[String])

  implicit val decoderBot: Decoder[Option[BotData]] =
    (c: HCursor) =>
      if (c.value.asObject.forall(_.isEmpty)) {
        Right(None)
      } else {
        Decoder[BotData].apply(c).map(Some(_))
      }
}
