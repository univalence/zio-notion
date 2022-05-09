package zio.notion.model.database
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.database.description.PropertyDescription.EmptyObject
import zio.notion.model.page.Page

@ConfiguredJsonCodec
final case class DatabaseQueryResponse(page: EmptyObject, nextCursor: Option[String], has_more: Boolean, results: List[Page]) //{ object: "page"; id: string }
