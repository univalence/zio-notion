package zio.notion.model.database.query.sort

import io.circe.Encoder

import zio.notion.model.database.query.Query

case class Sorts(sorts: List[Sort]) {
  def &&(other: Sort): Sorts = copy(sorts = sorts :+ other)
}

object Sorts {
  implicit val encoder: Encoder[Sorts] = Encoder[List[Sort]].contramap(_.sorts)
}
