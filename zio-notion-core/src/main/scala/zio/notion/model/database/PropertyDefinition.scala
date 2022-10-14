package zio.notion.model.database

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.database.metadata.{
  FormulaMetadata,
  NumberMetadata,
  RelationMetadata,
  RollupMetadata,
  SelectMetadata,
  StatusMetadata
}

@ConfiguredJsonCodec sealed trait PropertyDefinition {
  def id: String
}

object PropertyDefinition {
  final case class Title(id: String, name: String)                                    extends PropertyDefinition
  final case class People(id: String, name: String)                                   extends PropertyDefinition
  final case class Number(id: String, name: String, number: NumberMetadata)           extends PropertyDefinition
  final case class Formula(id: String, name: String, formula: FormulaMetadata)        extends PropertyDefinition
  final case class Select(id: String, name: String, select: SelectMetadata)           extends PropertyDefinition
  final case class MultiSelect(id: String, name: String, multiSelect: SelectMetadata) extends PropertyDefinition
  final case class Relation(id: String, name: String, relation: RelationMetadata)     extends PropertyDefinition
  final case class Rollup(id: String, name: String, rollup: RollupMetadata)           extends PropertyDefinition
  final case class RichText(id: String, name: String)                                 extends PropertyDefinition
  final case class Url(id: String, name: String)                                      extends PropertyDefinition
  final case class Files(id: String, name: String)                                    extends PropertyDefinition
  final case class Email(id: String, name: String)                                    extends PropertyDefinition
  final case class PhoneNumber(id: String, name: String)                              extends PropertyDefinition
  final case class Date(id: String, name: String)                                     extends PropertyDefinition
  final case class Checkbox(id: String, name: String)                                 extends PropertyDefinition
  final case class CreatedBy(id: String, name: String)                                extends PropertyDefinition
  final case class CreatedTime(id: String, name: String)                              extends PropertyDefinition
  final case class LastEditedBy(id: String, name: String)                             extends PropertyDefinition
  final case class LastEditedTime(id: String, name: String)                           extends PropertyDefinition
  final case class Status(id: String, name: String, status: StatusMetadata)           extends PropertyDefinition
}
