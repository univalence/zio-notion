package zio.notion.model.database.query.filter

import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

import zio.notion.model.database.query.filter.Filter.SubFilter

sealed trait Filter {
  self =>
  def or(subFilter: SubFilter): Filter = ???
}

object Filter {

  def where(subFilter: SubFilter): SubFilter      = ???
  def where(subFilter: PropertyFilter): SubFilter = ???
  def where(filter: Filter): SubFilter            = ???

  final case class Or(or: List[SubFilter])            extends Filter
  final case class And(and: List[SubFilter])          extends Filter
  final case class PropFilter(filter: PropertyFilter) extends Filter

  implicit val encoder: Encoder[Filter] = {
    case Or(or)             => Json.obj("or" -> or.asJson)
    case And(and)           => Json.obj("and" -> and.asJson)
    case PropFilter(filter) => filter.asJson
  }

  sealed trait SubFilter {
    self =>
    def or(other: SubFilter): Filter.Or      = Filter.Or(List(self, other))
    def or(other: PropertyFilter): Filter.Or = Filter.Or(List(self, SubFilter.PropFilter(other)))
  }

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
