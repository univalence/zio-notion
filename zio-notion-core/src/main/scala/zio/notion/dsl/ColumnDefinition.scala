package zio.notion.dsl

import zio.notion.model.database.PatchedPropertyDefinition

final case class ColumnDefinition(columnName: String) {
  def patch: PatchedColumnDefinition = PatchedColumnDefinition(columnName, PatchedPropertyDefinition.unit)
}
