package zio.notion.model.page.property.data

import io.circe.generic.extras._

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextFragment

@ConfiguredJsonCodec sealed trait RollupArrayDataType

object RollupArrayDataType {
  final case class TitleRollup(title: List[RichTextFragment])       extends RollupArrayDataType
  final case class RichTextRollup(richText: List[RichTextFragment]) extends RollupArrayDataType
  final case class PeopleRollup(people: List[Id])                   extends RollupArrayDataType
  final case class RelationRollup(relation: Id)                     extends RollupArrayDataType
}
