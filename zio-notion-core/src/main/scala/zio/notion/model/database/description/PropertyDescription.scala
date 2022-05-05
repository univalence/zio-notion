package zio.notion.model.database.description

import io.circe.JsonObject
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait PropertyDescription

object PropertyDescription {
  type EmptyObject = JsonObject

  final case class Title(id: String, name: String, title: EmptyObject)                          extends PropertyDescription
  final case class People(id: String, name: String, people: EmptyObject)                        extends PropertyDescription
  final case class Number(id: String, name: String, number: NumberDescription)                  extends PropertyDescription
  final case class Formula(id: String, name: String, formula: FormulaDescription)               extends PropertyDescription
  final case class Select(id: String, name: String, select: SelectDescription.Select)           extends PropertyDescription
  final case class MultiSelect(id: String, name: String, multiSelect: SelectDescription.Select) extends PropertyDescription
  final case class Relation(id: String, name: String, relation: RelationDescription.Relation)   extends PropertyDescription
  final case class Rollup(id: String, name: String, rollup: RollupDescription.Rollup)           extends PropertyDescription
  final case class RichText(id: String, name: String, richText: EmptyObject)                    extends PropertyDescription
  final case class Url(id: String, name: String, url: EmptyObject)                              extends PropertyDescription
  final case class Files(id: String, name: String, files: EmptyObject)                          extends PropertyDescription
  final case class Email(id: String, name: String, email: EmptyObject)                          extends PropertyDescription
  final case class PhoneNumber(id: String, name: String, phoneNumber: EmptyObject)              extends PropertyDescription
  final case class Date(id: String, name: String, date: EmptyObject)                            extends PropertyDescription
  final case class Checkbox(id: String, name: String, checkbox: EmptyObject)                    extends PropertyDescription
  final case class CreatedBy(id: String, name: String, createdBy: EmptyObject)                  extends PropertyDescription
  final case class CreatedTime(id: String, name: String, createdTime: EmptyObject)              extends PropertyDescription
  final case class LastEditedBy(id: String, name: String, lastEditedBy: EmptyObject)            extends PropertyDescription
  final case class LastEditedTime(id: String, name: String, lastEditedTime: EmptyObject)        extends PropertyDescription

}
