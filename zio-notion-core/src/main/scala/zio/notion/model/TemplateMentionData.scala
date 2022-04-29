package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait TemplateMentionData

object TemplateMentionData {
  final case class TemplateMentionDate(templateMentionDate: String)
  final case object TemplateMentionUser extends TemplateMentionData
}
