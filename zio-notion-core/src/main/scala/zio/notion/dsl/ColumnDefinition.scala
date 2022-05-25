package zio.notion.dsl

import zio.notion.model.database.PropertyDefinitionPatch

final case class ColumnDefinition(colName: String) {
  def patch: PatchedColumnDefinition = PatchedColumnDefinition(colName, PropertyDefinitionPatch.unit)
}
