package zio.notion.dsl

import zio.notion.model.database.PropertyDefinitionPatch

final case class ColumnDefinitions(predicate: String => Boolean) {
  def patch: PatchedColumnDefinitions = PatchedColumnDefinitions(predicate, PropertyDefinitionPatch.unit)
}
