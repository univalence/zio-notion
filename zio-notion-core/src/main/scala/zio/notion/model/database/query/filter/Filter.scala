package zio.notion.model.database.query.filter

import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

//{
//  "and": [
//    {
//      "property": "Done",
//      "checkbox": {
//        "equals": true
//      }
//    },
//    {
//      "or": [
//        {
//          "property": "Tags",
//          "contains": "A"
//        },
//        {
//          "property": "Tags",
//          "contains": "B"
//        }
//      ]
//    }
//  ]
//}

//https://developers.notion.com/reference/post-database-query

sealed trait Filter

object Filter {
  final case class Or(or: List[SubFilter])            extends Filter
  final case class And(and: List[SubFilter])          extends Filter
  final case class PropFilter(filter: PropertyFilter) extends Filter

  implicit val encoder: Encoder[Filter] = {
    case Or(or)             => Json.obj("or" -> or.asJson)
    case And(and)           => Json.obj("and" -> and.asJson)
    case PropFilter(filter) => filter.asJson
  }

  sealed trait SubFilter

  object SubFilter {
    final case class Or(or: List[PropertyFilter])       extends SubFilter
    final case class And(and: List[PropertyFilter])     extends SubFilter
    final case class PropFilter(filter: PropertyFilter) extends SubFilter

    implicit val encoder: Encoder[SubFilter] = {
      case Or(or)             => Json.obj("or" -> or.asJson)
      case And(and)           => Json.obj("and" -> and.asJson)
      case PropFilter(filter) => filter.asJson
    }
  }
}
