package zio.notion.model.common.richtext

import io.circe._
import io.circe.Decoder.Result
import io.circe.generic.extras._
import io.circe.syntax.EncoderOps

import zio.notion.model.common.{Id, Period, TimePeriod, Url}
import zio.notion.model.common.richtext.RichTextFragment.{Equation, Mention, Text}
import zio.notion.model.common.richtext.RichTextFragment.Mention.MentionData

@ConfiguredJsonCodec sealed trait RichTextFragment { self =>

  override def toString: String =
    self match {
      case Text(_, _, plainText, _) => plainText
      case Mention(mention, _, plainText, href) =>
        mention match {
          case MentionData.User(_)            => plainText
          case MentionData.LinkPreview(_)     => href.get
          case MentionData.Page(_)            => href.get
          case MentionData.Database(_)        => href.get
          case MentionData.Date(date)         => date.toString()
          case MentionData.DateTime(date)     => date.toString()
          case MentionData.TemplateMention(_) => href.get
        }
      case Equation(_, _, plainText, _) => plainText
    }
}

object RichTextFragment {

  final case class Text(
      text:        Text.TextData,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextFragment

  object Text {
    @ConfiguredJsonCodec final case class TextData(content: String, link: Option[Url])
  }

  final case class Mention(
      mention:     Mention.MentionData,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextFragment

  object Mention {
    sealed trait MentionData

    object MentionData {
      @ConfiguredJsonCodec final case class User(user: Id)                                         extends MentionData
      @ConfiguredJsonCodec final case class LinkPreview(linkPreview: Url)                          extends MentionData
      @ConfiguredJsonCodec final case class Page(page: Id)                                         extends MentionData
      @ConfiguredJsonCodec final case class Database(database: Id)                                 extends MentionData
      @ConfiguredJsonCodec final case class Date(date: Period)                                     extends MentionData
      @ConfiguredJsonCodec final case class DateTime(date: TimePeriod)                             extends MentionData
      @ConfiguredJsonCodec final case class TemplateMention(templateMention: TemplateMention.Data) extends MentionData

      object TemplateMention {
        @ConfiguredJsonCodec sealed trait Data

        object Data {
          final case class TemplateMentionDate(templateMentionDate: String) extends Data
          final case object TemplateMentionUser                             extends Data
        }
      }

      implicit val mentionCodec: Codec[MentionData] =
        new Codec[MentionData] {

          override def apply(c: HCursor): Result[MentionData] =
            c.downField("type").as[String] match {
              case Right(value) =>
                value match {
                  case "user"             => Decoder[User].apply(c)
                  case "link_preview"     => Decoder[LinkPreview].apply(c)
                  case "page"             => Decoder[Page].apply(c)
                  case "database"         => Decoder[Database].apply(c)
                  case "date"             => Decoder[Date].apply(c) orElse Decoder[DateTime].apply(c)
                  case "template_mention" => Decoder[TemplateMention].apply(c)
                  case v                  => Left(DecodingFailure(s"The type $v is unknown", c.history))
                }
              case Left(_) => Left(DecodingFailure(s"Missing required field 'type'", c.history))
            }

          override def apply(data: MentionData): Json =
            data match {
              case a: User            => a.asJson deepMerge Json.obj("type" -> Json.fromString("user"))
              case a: LinkPreview     => a.asJson deepMerge Json.obj("type" -> Json.fromString("link_preview"))
              case a: Page            => a.asJson deepMerge Json.obj("type" -> Json.fromString("page"))
              case a: Database        => a.asJson deepMerge Json.obj("type" -> Json.fromString("database"))
              case a: Date            => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
              case a: DateTime        => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
              case a: TemplateMention => a.asJson deepMerge Json.obj("type" -> Json.fromString("template_mention"))
            }
        }
    }
  }

  final case class Equation(
      expression:  Equation.Expression,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextFragment

  object Equation {
    @ConfiguredJsonCodec final case class Expression(expression: String)
  }

  def default(text: String, annotations: Annotations): Text =
    RichTextFragment.Text(RichTextFragment.Text.TextData(text, None), annotations, text, None)
}
