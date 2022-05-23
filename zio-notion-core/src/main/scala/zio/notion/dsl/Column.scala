package zio.notion.dsl

import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting.Property

final case class Column(colName: String) {
  def ascending: Sorting  = Property(colName, ascending = true)
  def descending: Sorting = Property(colName, ascending = false)

  def definition: ColumnDefinition = colDefinition(colName)
}

final case class Columns(predicate: String => Boolean) {
  def definition: ColumnDefinitions = columnDefinitionsMatching(predicate)
}
