package zio.notion.dsl

import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting.Property

case class Column(name: String) {
  def ascending: Sorting  = Property(name, ascending = true)
  def descending: Sorting = Property(name, ascending = false)
}
