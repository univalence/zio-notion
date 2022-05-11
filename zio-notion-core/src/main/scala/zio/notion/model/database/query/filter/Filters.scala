package zio.notion.model.database.query.filter

import io.circe.Encoder

case class Filters(filter: List[Filter])

object Filters {
  implicit val encoder: Encoder[Filters] = Encoder[List[Filter]].contramap(_.filter)
}
