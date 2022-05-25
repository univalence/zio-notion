package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher
import zio.notion.model.database.PropertyDefinitionPatch
import zio.notion.model.database.PropertyDefinitionPatch.PropertySchema

sealed trait PatchedDefinition {
  def patch: PropertyDefinitionPatch
  def matcher: ColumnMatcher
}

final case class PatchedColumnDefinition(colName: String, patch: PropertyDefinitionPatch) extends PatchedDefinition {
  def rename(name: String): PatchedColumnDefinition = copy(patch = patch.copy(name = Some(name)))

  def as(propertySchema: PropertySchema): PatchedColumnDefinition = copy(patch = patch.copy(propertySchema = Some(propertySchema)))

  override def matcher: ColumnMatcher = ColumnMatcher.One(colName)
}

final case class PatchedColumnDefinitions(predicate: String => Boolean, patch: PropertyDefinitionPatch) extends PatchedDefinition {
  def as(propertySchema: PropertySchema): PatchedColumnDefinitions = copy(patch = patch.copy(propertySchema = Some(propertySchema)))

  override def matcher: ColumnMatcher = ColumnMatcher.Predicate(predicate)
}
