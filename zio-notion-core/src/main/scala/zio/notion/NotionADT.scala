package zio.notion

import zio.json._

sealed trait NotionADT
final case class RichTextElement(
)

object NotionADT {
  type IdRequest = String

  @jsonDiscriminator("type") sealed trait Parent

  object Parent {
    @jsonHint("page_id")
    final case class Page(@jsonField("page_id") pageId: String) extends Parent

    @jsonHint("database_id")
    final case class Database(@jsonField("database_id") databaseId: String) extends Parent

    @jsonHint("workspace")
    case object Workspace extends Parent

    implicit val decoder: JsonDecoder[Parent] = DeriveJsonDecoder.gen[Parent]
    implicit val encoder: JsonEncoder[Parent] = DeriveJsonEncoder.gen[Parent]
  }
}
