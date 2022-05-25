package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher.Predicate
import zio.notion.model.database.PatchedPropertyDefinition

final case class ColumnDefinitions(predicate: String => Boolean) {
  def patch: PatchedColumnDefinition = PatchedColumnDefinition(Predicate(predicate), PatchedPropertyDefinition.unit)
}
