package zio.notion.model
import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait RollupArrayDataType

object RollupArrayDataType {
  final case class TitleRollup(title: List[RichTextData])       extends RollupArrayDataType
  final case class RichTextRollup(richText: List[RichTextData]) extends RollupArrayDataType
  final case class PeopleRollup(people: List[UserId])           extends RollupArrayDataType
  final case class RelationRollup(relation: Id)                 extends RollupArrayDataType
}
