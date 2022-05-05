package zio.notion.model.common.richtext

import io.circe.generic.extras._

import zio.notion.model.common.{Url, UserId}
import zio.notion.model.page.properties.data.Id

@ConfiguredJsonCodec sealed trait RichTextData

object RichTextData {
  @ConfiguredJsonCodec final case class Text(text: Text.TextData, annotations: Annotations, plainText: String, href: Option[String]) extends RichTextData

  object Text {
    @ConfiguredJsonCodec final case class TextData(content: String, link: Option[Url])
  }

  final case class Mention(mention: Mention.MentionData, annotations: Annotations, plainText: String, href: Option[String]) extends RichTextData

  object Mention {
    @ConfiguredJsonCodec sealed trait MentionData

    object MentionData {
      final case class User(user: UserId)            extends MentionData
      final case class LinkPreview(linkPreview: Url) extends MentionData
      final case class Page(page: Id)                extends MentionData
      final case class Database(database: Id)        extends MentionData

      final case class TemplateMention(templateMention: TemplateMention.TemplateMentionData)

      object TemplateMention {
        sealed trait TemplateMentionData

        object TemplateMentionData {
          final case class TemplateMentionDate(templateMentionDate: String)
          final case object TemplateMentionUser extends TemplateMentionData
        }
      }
    }
  }

  final case class Equation(expression: Equation.Expression, annotations: Annotations, plainText: String, href: Option[String]) extends RichTextData

  object Equation {
    @ConfiguredJsonCodec final case class Expression(expression: String)
  }

}
