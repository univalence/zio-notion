package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher.One
import zio.notion.model.database.PatchedPropertyDefinition

final case class ColumnDefinition(colName: String) {
  def patch: PatchedColumnDefinition = PatchedColumnDefinition(One(colName), PatchedPropertyDefinition.unit)
}
