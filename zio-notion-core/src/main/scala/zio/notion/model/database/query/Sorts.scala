package zio.notion.model.database.query

import io.circe.{Encoder, Json}

import zio.notion.model.database.query.Sorts.Sorting

final case class Sorts(sort: Seq[Sorting]) {
  def andThen(other: Sorts): Sorts = copy(sort = sort ++ other.sort)

  def andThen(sorting: Sorting): Sorts = copy(sort = sort :+ sorting)
}

object Sorts {
  sealed trait Sorting

  object Sorting {
    final case class Property(property: String, ascending: Boolean)          extends Sorting
    final case class Timestamp(timestamp: TimestampType, ascending: Boolean) extends Sorting

    sealed trait TimestampType {
      self =>
      def ascending: Sorting  = Timestamp(self, ascending = true)
      def descending: Sorting = Timestamp(self, ascending = false)
    }

    object TimestampType {
      final case object CreatedTime    extends TimestampType
      final case object LastEditedTime extends TimestampType
    }

    private def directionEncoding(ascending: Boolean): Json =
      if (ascending) Json.fromString("ascending")
      else Json.fromString("descending")

    implicit val encoder: Encoder[Sorting] = {
      case Property(property, ascending) =>
        Json.obj(
          "property"  -> Json.fromString(property),
          "direction" -> directionEncoding(ascending)
        )
      case Timestamp(timestamp, ascending) =>
        Json.obj(
          "property" -> {
            timestamp match {
              case TimestampType.CreatedTime    => Json.fromString("created_time")
              case TimestampType.LastEditedTime => Json.fromString("last_edited_time")
            }
          },
          "direction" -> directionEncoding(ascending)
        )
    }
  }

  implicit val encoder: Encoder[Sorts] = Encoder[Seq[Sorting]].contramap(_.sort)
}
