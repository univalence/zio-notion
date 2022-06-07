package zio.notion.dsl

import zio.notion.model.database.Database.Patch.Operations.Operation.{CreateColumn, RemoveColumn, UpdateColumn}
import zio.notion.model.database.PatchedPropertyDefinition
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema

final case class ColumnDefinition(columnName: String) {
  def patch: UpdateColumn = UpdateColumn(columnName, PatchedPropertyDefinition.unit)

  def create: ColumnDefinition.Create = ColumnDefinition.Create(columnName)

  def remove: RemoveColumn = RemoveColumn(columnName)
}

object ColumnDefinition {

  final case class Create(columnName: String) {
    def as(propertySchema: PropertySchema): CreateColumn = CreateColumn(columnName, propertySchema)
  }
}
