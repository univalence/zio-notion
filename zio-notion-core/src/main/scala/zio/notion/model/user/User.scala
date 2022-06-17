package zio.notion.model.user

import io.circe.{Codec, Decoder, HCursor, Json}
import io.circe.Decoder.Result
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps

sealed trait User { self =>
  def id: String

  override def toString: String =
    self match {
      case User.Hidden(id)                  => id
      case User.Person(id, name, _, person) => name.orElse(person.email).getOrElse(id)
      case User.Bot(id, name, _, _)         => name.getOrElse(id)
    }
}

object User {

  @ConfiguredJsonCodec
  final case class Hidden(id: String) extends User

  @ConfiguredJsonCodec
  final case class Person(id: String, name: Option[String], avatarUrl: Option[String], person: Person.Data) extends User

  @ConfiguredJsonCodec
  final case class Bot(id: String, name: Option[String], avatarUrl: Option[String], bot: Option[Bot.Data]) extends User

  object Person {
    @ConfiguredJsonCodec final case class Data(email: Option[String])
  }

  object Bot {
    @ConfiguredJsonCodec final case class Data(owner: Owner)

    implicit val decoderBot: Decoder[Option[Data]] =
      (c: HCursor) => if (c.value.asObject.forall(_.isEmpty)) Right(None) else Decoder[Data].apply(c).map(Some(_))
  }

  implicit val coderUser: Codec[User] =
    new Codec[User] {

      override def apply(c: HCursor): Result[User] =
        c.downField("type").as[String] match {
          case Left(_) => Decoder[Hidden].apply(c)
          case Right(value) =>
            value match {
              case "person" => Decoder[Person].apply(c)
              case "bot"    => Decoder[Bot].apply(c)
              case _        => Decoder[Hidden].apply(c)
            }
        }

      override def apply(user: User): Json =
        user match {
          case u: Hidden => u.asJson
          case u: Person => u.asJson.deepMerge(Json.obj("type" -> Json.fromString("person")))
          case u: Bot    => u.asJson.deepMerge(Json.obj("type" -> Json.fromString("bot")))
        }
    }
}
