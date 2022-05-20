package zio.notion.model.database.query

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Query(filter: Option[Filter], sorts: Option[Sorts])

object Query {
  val empty: Query = Query(None, None)

  implicit val encoder: Encoder[Query] = deriveEncoder[Query]
}
