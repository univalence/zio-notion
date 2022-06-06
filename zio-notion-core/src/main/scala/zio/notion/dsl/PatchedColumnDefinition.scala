package zio.notion.dsl

import zio.notion.model.database.PatchedPropertyDefinition
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema

final case class PatchedColumnDefinition(columnName: String, patch: PatchedPropertyDefinition) {
  def rename(name: String): PatchedColumnDefinition = copy(patch = patch.copy(name = Some(name)))

  def as(propertySchema: PropertySchema): PatchedColumnDefinition = copy(patch = patch.copy(propertySchema = Some(propertySchema)))
}
