package zio.notion.model.database.query.filter

sealed trait Filter

object Filter {
  final case class Or(or: List[Filter])             extends Filter
  final case class And(and: List[Filter])           extends Filter
  final case class Property(filter: PropertyFilter) extends Filter
}
