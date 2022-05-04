package zio.notion.model.page.properties.data

import io.circe.generic.extras._

import zio.notion.model.common.UserId
import zio.notion.model.common.rich_text.RichTextData

@ConfiguredJsonCodec sealed trait RollupArrayDataType

object RollupArrayDataType {
  final case class TitleRollup(title: List[RichTextData])       extends RollupArrayDataType
  final case class RichTextRollup(richText: List[RichTextData]) extends RollupArrayDataType
  final case class PeopleRollup(people: List[UserId])           extends RollupArrayDataType
  final case class RelationRollup(relation: Id)                 extends RollupArrayDataType
}
