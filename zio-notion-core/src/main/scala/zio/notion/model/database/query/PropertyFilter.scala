package zio.notion.model.database.query

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.magnolia.PatchedPropertyEncoderDerivation

@ConfiguredJsonCodec(encodeOnly = true) sealed trait PropertyFilter

object PropertyFilter {
  final case class Title(property: String, title: TextPropertyFilter)                   extends PropertyFilter
  final case class RichText(property: String, richText: TextPropertyFilter)             extends PropertyFilter
  final case class Number(property: String, number: NumberPropertyFilter)               extends PropertyFilter
  final case class Checkbox(property: String, checkbox: CheckboxPropertyFilter)         extends PropertyFilter
  final case class Select(property: String, select: SelectPropertyFilter)               extends PropertyFilter
  final case class MultiSelect(multiSelect: TextPropertyFilter, property: String)       extends PropertyFilter
  final case class Date(date: DatePropertyFilter, property: String)                     extends PropertyFilter
  final case class People(people: PeoplePropertyFilter, property: String)               extends PropertyFilter
  final case class Files(files: ExistencePropertyFilter, property: String)              extends PropertyFilter
  final case class Url(url: TextPropertyFilter, property: String)                       extends PropertyFilter
  final case class Email(title: TextPropertyFilter, property: String)                   extends PropertyFilter
  final case class PhoneNumber(phoneNumber: TextPropertyFilter, property: String)       extends PropertyFilter
  final case class Relation(relation: RelationPropertyFilter, property: String)         extends PropertyFilter
  final case class CreatedBy(createdBy: PeoplePropertyFilter, property: String)         extends PropertyFilter
  final case class CreatedTime(createdTime: DatePropertyFilter, property: String)       extends PropertyFilter
  final case class LastEditedBy(lastEditedBy: DatePropertyFilter, property: String)     extends PropertyFilter
  final case class LastEditedTime(lastEditedTime: DatePropertyFilter, property: String) extends PropertyFilter
  final case class Formula(formula: FormulaPropertyFilter, property: String)            extends PropertyFilter
  final case class Rollup(rollup: RollupPropertyFilter, property: String)               extends PropertyFilter

  sealed trait ExistencePropertyFilter

  object ExistencePropertyFilter {
    implicit val encoder: Encoder[ExistencePropertyFilter] = PatchedPropertyEncoderDerivation.gen[ExistencePropertyFilter]
  }

  sealed trait TextPropertyFilter

  object TextPropertyFilter {
    final case class StartsWith(startsWith: String) extends TextPropertyFilter
    final case class EndsWith(endsWith: String)     extends TextPropertyFilter

    implicit val encoder: Encoder[TextPropertyFilter] = PatchedPropertyEncoderDerivation.gen[TextPropertyFilter]
  }

  sealed trait NumberPropertyFilter

  object NumberPropertyFilter {
    final case class Equals(equals: Double)                             extends NumberPropertyFilter
    final case class DoesNotEqual(doesNotEqual: Double)                 extends NumberPropertyFilter
    final case class GreaterThan(greaterThan: Double)                   extends NumberPropertyFilter
    final case class LessThan(lessThan: Double)                         extends NumberPropertyFilter
    final case class GreaterThanOrEqualTo(greaterThanOrEqualTo: Double) extends NumberPropertyFilter
    final case class LessThanOrEqualTo(lessThanOrEqualTo: Double)       extends NumberPropertyFilter

    implicit val encoder: Encoder[NumberPropertyFilter] = PatchedPropertyEncoderDerivation.gen[NumberPropertyFilter]
  }

  sealed trait CheckboxPropertyFilter

  object CheckboxPropertyFilter {
    final case class Equals(equals: Boolean)             extends CheckboxPropertyFilter
    final case class DoesNotEqual(doesNotEqual: Boolean) extends CheckboxPropertyFilter

    implicit val encoder: Encoder[CheckboxPropertyFilter] = PatchedPropertyEncoderDerivation.gen[CheckboxPropertyFilter]
  }

  sealed trait SelectPropertyFilter

  object SelectPropertyFilter {
    implicit val encoder: Encoder[SelectPropertyFilter] = PatchedPropertyEncoderDerivation.gen[SelectPropertyFilter]
  }

  sealed trait MultiSelectPropertyFilter

  object MultiSelectPropertyFilter {
    implicit val encoder: Encoder[MultiSelectPropertyFilter] = PatchedPropertyEncoderDerivation.gen[MultiSelectPropertyFilter]
  }

  sealed trait DatePropertyFilter

  object DatePropertyFilter {
    final case class Before(before: String)         extends DatePropertyFilter
    final case class After(after: String)           extends DatePropertyFilter
    final case class OnOrBefore(onOrBefore: String) extends DatePropertyFilter
    final case class OnOrAfter(onOrAfter: String)   extends DatePropertyFilter
    final case object PastWeek                      extends DatePropertyFilter
    final case object PastMonth                     extends DatePropertyFilter
    final case object NextWeek                      extends DatePropertyFilter
    final case object NextMonth                     extends DatePropertyFilter
    final case object NextYear                      extends DatePropertyFilter

    implicit val encoder: Encoder[DatePropertyFilter] = PatchedPropertyEncoderDerivation.gen[DatePropertyFilter]
  }

  sealed trait PeoplePropertyFilter

  object PeoplePropertyFilter {
    implicit val encoder: Encoder[PeoplePropertyFilter] = PatchedPropertyEncoderDerivation.gen[PeoplePropertyFilter]
  }

  sealed trait RelationPropertyFilter

  object RelationPropertyFilter {
    implicit val encoder: Encoder[RelationPropertyFilter] = PatchedPropertyEncoderDerivation.gen[RelationPropertyFilter]
  }

  sealed trait FormulaPropertyFilter

  object FormulaPropertyFilter {

    final case class StringFormula(string: TextPropertyFilter)         extends FormulaPropertyFilter
    final case class CheckboxFormula(checkbox: CheckboxPropertyFilter) extends FormulaPropertyFilter
    final case class numberFormula(number: NumberPropertyFilter)       extends FormulaPropertyFilter
    final case class DateFormula(date: DatePropertyFilter)             extends FormulaPropertyFilter

    implicit val encoder: Encoder[FormulaPropertyFilter] = PatchedPropertyEncoderDerivation.gen[FormulaPropertyFilter]
  }

  sealed trait RollupSubFilterPropertyFilter

  object RollupSubFilterPropertyFilter {
    final case class RichtextRollupSubfilter(richText: TextPropertyFilter)              extends RollupSubFilterPropertyFilter
    final case class NumberRollupSubfilter(number: NumberPropertyFilter)                extends RollupSubFilterPropertyFilter
    final case class CheckboxRollupSubfilter(checkbox: CheckboxPropertyFilter)          extends RollupSubFilterPropertyFilter
    final case class SelectRollupSubfilter(select: SelectPropertyFilter)                extends RollupSubFilterPropertyFilter
    final case class MultiSelectRollupSubfilter(multiSelect: MultiSelectPropertyFilter) extends RollupSubFilterPropertyFilter
    final case class RelationRollupSubfilter(relation: RelationPropertyFilter)          extends RollupSubFilterPropertyFilter
    final case class DateRollupSubfilter(date: DatePropertyFilter)                      extends RollupSubFilterPropertyFilter
    final case class PeopleRollupSubfilter(people: PeoplePropertyFilter)                extends RollupSubFilterPropertyFilter
    final case class FilesRollupSubfilter(files: ExistencePropertyFilter)               extends RollupSubFilterPropertyFilter

    implicit val encoder: Encoder[RollupSubFilterPropertyFilter] = PatchedPropertyEncoderDerivation.gen[RollupSubFilterPropertyFilter]
  }

  sealed trait RollupPropertyFilter

  object RollupPropertyFilter {
    final case class Any(any: RollupSubFilterPropertyFilter)     extends RollupPropertyFilter
    final case class None(none: RollupSubFilterPropertyFilter)   extends RollupPropertyFilter
    final case class Every(every: RollupSubFilterPropertyFilter) extends RollupPropertyFilter
    final case class date(date: DatePropertyFilter)              extends RollupPropertyFilter
    final case class NumberRollup(number: NumberPropertyFilter)  extends RollupPropertyFilter

    implicit val encoder: Encoder[RollupPropertyFilter] = PatchedPropertyEncoderDerivation.gen[RollupPropertyFilter]
  }

  final case class Equals(equals: String)             extends TextPropertyFilter with SelectPropertyFilter with DatePropertyFilter
  final case class DoesNotEqual(doesNotEqual: String) extends TextPropertyFilter with SelectPropertyFilter

  final case class Contains(contains: String)
      extends TextPropertyFilter
      with MultiSelectPropertyFilter
      with PeoplePropertyFilter
      with RelationPropertyFilter
  final case class DoesNotContain(doesNotContain: String)
      extends TextPropertyFilter
      with MultiSelectPropertyFilter
      with PeoplePropertyFilter
      with RelationPropertyFilter

  final case class IsEmpty(isEmpty: Boolean)
      extends ExistencePropertyFilter
      with TextPropertyFilter
      with NumberPropertyFilter
      with SelectPropertyFilter
      with MultiSelectPropertyFilter
      with DatePropertyFilter
      with PeoplePropertyFilter
      with RelationPropertyFilter

  final case class IsNotEmpty(isNotEmpty: Boolean)
      extends ExistencePropertyFilter
      with TextPropertyFilter
      with NumberPropertyFilter
      with SelectPropertyFilter
      with MultiSelectPropertyFilter
      with DatePropertyFilter
      with PeoplePropertyFilter
      with RelationPropertyFilter
}
