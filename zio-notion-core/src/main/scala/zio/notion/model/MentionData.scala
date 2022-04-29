package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait MentionData

object MentionData {
  final case class User(user: UserId)            extends MentionData
  final case class LinkPreview(linkPreview: Url) extends MentionData
  final case class TemplateMention(templateMention: TemplateMentionData)
  final case class Page(page: Id)         extends MentionData
  final case class Database(database: Id) extends MentionData

}
