package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher
import zio.notion.model.database.patch.PatchPlan
import zio.notion.model.database.patch.PatchPlan.PropertyType

trait Definition {
  def patchPlan: PatchPlan
  def matcher: ColumnMatcher
}

final case class ColumnDefinition(colName: String, patchPlan: PatchPlan) extends Definition {
  def rename(name: String): ColumnDefinition = copy(patchPlan = patchPlan.copy(name = Some(name)))

  def as(propertyType: PropertyType): ColumnDefinition = copy(patchPlan = patchPlan.copy(propertyType = Some(propertyType)))

  override def matcher: ColumnMatcher = ColumnMatcher.One(colName)
}

final case class ColumnDefinitions(predicate: String => Boolean, patchPlan: PatchPlan) extends Definition {
  def as(propertyType: PropertyType): ColumnDefinitions = copy(patchPlan = patchPlan.copy(propertyType = Some(propertyType)))

  override def matcher: ColumnMatcher = ColumnMatcher.Predicate(predicate)
}
