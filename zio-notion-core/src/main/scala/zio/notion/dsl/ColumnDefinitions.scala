package zio.notion.dsl

import zio.notion.model.database.patch.PatchPlan

final case class ColumnDefinitions(predicate: String => Boolean) {
  def patch: PatchedColumnDefinitions = PatchedColumnDefinitions(predicate, PatchPlan.unit)
}
