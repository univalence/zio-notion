package zio.notion.model.database.query

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

import zio.notion.model.database.query.filter.Filters
import zio.notion.model.database.query.sort.Sorts

final case class Query(filters: Option[Filters], sorts: Option[Sorts])

object Query {
  implicit val encoder: Encoder[Query] = deriveEncoder[Query]
}
