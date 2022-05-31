package zio.notion.model.common.richtext

import io.circe.generic.extras._

import zio.notion.model.common.{Id, Url}
import zio.notion.model.page.property.data.DateData

@ConfiguredJsonCodec sealed trait RichTextData

object RichTextData {

  final case class Text(
      text:        Text.TextData,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextData

  object Text {
    @ConfiguredJsonCodec final case class TextData(content: String, link: Option[Url])
  }

  final case class Mention(
      mention:     Mention.MentionData,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextData

  object Mention {
    @ConfiguredJsonCodec sealed trait MentionData

    object MentionData {
      final case class User(user: Id)                                                        extends MentionData
      final case class LinkPreview(linkPreview: Url)                                         extends MentionData
      final case class Page(page: Id)                                                        extends MentionData
      final case class Database(database: Id)                                                extends MentionData
      final case class Date(date: DateData)                                                  extends MentionData
      final case class TemplateMention(templateMention: TemplateMention.TemplateMentionData) extends MentionData

      object TemplateMention {
        @ConfiguredJsonCodec sealed trait TemplateMentionData

        object TemplateMentionData {
          final case class TemplateMentionDate(templateMentionDate: String)
          final case object TemplateMentionUser extends TemplateMentionData
        }
      }
    }
  }

  final case class Equation(
      expression:  Equation.Expression,
      annotations: Annotations,
      plainText:   String,
      href:        Option[String]
  ) extends RichTextData

  object Equation {
    @ConfiguredJsonCodec final case class Expression(expression: String)
  }

  def default(text: String, annotations: Annotations): Text =
    RichTextData.Text(RichTextData.Text.TextData(text, None), annotations, text, None)
}
