package zio.notion.model.database.query.sort

import io.circe.{Encoder, Json}

sealed trait Sort {
  self =>
  def &&(other: Sort): Sorts = Sorts(List(self, other))
}

object Sort {
  final case class Property(property: String, ascending: Boolean)          extends Sort
  final case class Timestamp(timestamp: TimestampType, ascending: Boolean) extends Sort

  implicit class StringOps(string: String) {
    def ascending: Property  = Property(string, ascending = true)
    def descending: Property = Property(string, ascending = false)
  }

  implicit val encoder: Encoder[Sort] = {
    case Property(property, ascending) =>
      Json.obj(
        "property" -> Json.fromString(property),
        "direction" -> {
          if (ascending) Json.fromString("ascending")
          else Json.fromString("descending")
        }
      )
    case Timestamp(timestamp, ascending) =>
      Json.obj(
        "property" -> {
          timestamp match {
            case TimestampType.CreatedTime    => Json.fromString("created_time")
            case TimestampType.LastEditedTime => Json.fromString("last_edited_time")
          }
        },
        "direction" -> {
          if (ascending) Json.fromString("ascending")
          else Json.fromString("descending")
        }
      )
  }
}
