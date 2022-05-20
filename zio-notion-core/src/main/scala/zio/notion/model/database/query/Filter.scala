package zio.notion.model.database.query

import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

sealed trait Filter {
  def and(other: Filter): Filter
  def or(other: Filter): Filter
}

object Filter {
  final case class And(filters: Seq[Filter]) extends Filter { self =>
    override def and(other: Filter): Filter =
      other match {
        case And(otherFilters) => copy(filters = filters ++ otherFilters)
        case _: Or             => Or(self :: other :: Nil)
        case filter: One       => copy(filters = filters :+ filter)
      }

    override def or(other: Filter): Filter = Or(List(self, other))
  }

  object And {
    def apply(filter: Filter, filters: Filter*): And = And(filter +: filters)
  }

  final case class Or(filters: Seq[Filter]) extends Filter { self =>
    override def and(other: Filter): Filter = And(List(self, other))

    override def or(other: Filter): Filter =
      other match {
        case Or(otherFilters) => copy(filters = filters ++ otherFilters)
        case _: And           => And(self :: other :: Nil)
        case _: One           => copy(filters = filters :+ other)
      }
  }

  object Or {
    def apply(filter: Filter, filters: Filter*): Or = Or(filter +: filters)
  }

  final case class One(filter: PropertyFilter) extends Filter { self =>
    override def and(other: Filter): Filter =
      other match {
        case And(filters) => And(self +: filters)
        case _            => And(self :: other :: Nil)
      }

    override def or(other: Filter): Filter =
      other match {
        case Or(filters) => Or(self +: filters)
        case _           => Or(self :: other :: Nil)
      }
  }

  implicit val encoder: Encoder[Filter] = {
    case Or(filters)  => Json.obj("or" -> filters.map(encoder.apply).asJson)
    case And(filters) => Json.obj("and" -> filters.map(encoder.apply).asJson)
    case One(filter)  => filter.asJson
  }
}
