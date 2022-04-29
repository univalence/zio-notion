package zio.notion.model

import zio.json._

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
