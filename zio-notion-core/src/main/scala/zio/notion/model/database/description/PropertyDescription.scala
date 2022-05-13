package zio.notion.model.database.description

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait PropertyDescription

object PropertyDescription {
  final case class Title(id: String, name: String)                                              extends PropertyDescription
  final case class People(id: String, name: String)                                             extends PropertyDescription
  final case class Number(id: String, name: String, number: NumberDescription)                  extends PropertyDescription
  final case class Formula(id: String, name: String, formula: FormulaDescription)               extends PropertyDescription
  final case class Select(id: String, name: String, select: SelectDescription.Select)           extends PropertyDescription
  final case class MultiSelect(id: String, name: String, multiSelect: SelectDescription.Select) extends PropertyDescription
  final case class Relation(id: String, name: String, relation: RelationDescription.Relation)   extends PropertyDescription
  final case class Rollup(id: String, name: String, rollup: RollupDescription.Rollup)           extends PropertyDescription
  final case class RichText(id: String, name: String)                                           extends PropertyDescription
  final case class Url(id: String, name: String)                                                extends PropertyDescription
  final case class Files(id: String, name: String)                                              extends PropertyDescription
  final case class Email(id: String, name: String)                                              extends PropertyDescription
  final case class PhoneNumber(id: String, name: String)                                        extends PropertyDescription
  final case class Date(id: String, name: String)                                               extends PropertyDescription
  final case class Checkbox(id: String, name: String)                                           extends PropertyDescription
  final case class CreatedBy(id: String, name: String)                                          extends PropertyDescription
  final case class CreatedTime(id: String, name: String)                                        extends PropertyDescription
  final case class LastEditedBy(id: String, name: String)                                       extends PropertyDescription
  final case class LastEditedTime(id: String, name: String)                                     extends PropertyDescription

}
