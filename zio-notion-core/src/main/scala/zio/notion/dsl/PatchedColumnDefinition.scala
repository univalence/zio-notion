package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher
import zio.notion.model.database.patch.PatchPlan
import zio.notion.model.database.patch.PatchPlan.PropertyType

sealed trait PatchedDefinition {
  def patchPlan: PatchPlan
  def matcher: ColumnMatcher
}

final case class PatchedColumnDefinition(colName: String, patchPlan: PatchPlan) extends PatchedDefinition {
  def rename(name: String): PatchedColumnDefinition = copy(patchPlan = patchPlan.copy(name = Some(name)))

  def as(propertyType: PropertyType): PatchedColumnDefinition = copy(patchPlan = patchPlan.copy(propertyType = Some(propertyType)))

  override def matcher: ColumnMatcher = ColumnMatcher.One(colName)
}

final case class PatchedColumnDefinitions(predicate: String => Boolean, patchPlan: PatchPlan) extends PatchedDefinition {
  def as(propertyType: PropertyType): PatchedColumnDefinitions = copy(patchPlan = patchPlan.copy(propertyType = Some(propertyType)))

  override def matcher: ColumnMatcher = ColumnMatcher.Predicate(predicate)
}
