package zio.notion.dsl

import zio.notion.model.database.patch.PatchPlan

final case class ColumnDefinition(colName: String) {
  def patch: PatchedColumnDefinition = PatchedColumnDefinition(colName, PatchPlan.unit)
}
