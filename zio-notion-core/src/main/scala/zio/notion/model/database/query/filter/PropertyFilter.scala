package zio.notion.model.database.query.filter

sealed trait PropertyFilter
sealed trait ExistencePropertyFilter       extends PropertyFilter
sealed trait TextPropertyFilter            extends PropertyFilter
sealed trait NumberPropertyFilter          extends PropertyFilter
sealed trait CheckboxPropertyFilter        extends PropertyFilter
sealed trait SelectPropertyFilter          extends PropertyFilter
sealed trait MultiSelectPropertyFilter     extends PropertyFilter
sealed trait DatePropertyFilter            extends PropertyFilter
sealed trait PeoplePropertyFilter          extends PropertyFilter
sealed trait RelationPropertyFilter        extends PropertyFilter
sealed trait FormulaPropertyFilter         extends PropertyFilter
sealed trait RollupSubFilterPropertyFilter extends PropertyFilter
sealed trait RollupPropertyFilter          extends PropertyFilter

object PropertyFilter {

  final case class Title(title: TextPropertyFilter, property: String)                   extends PropertyFilter
  final case class RichText(richText: TextPropertyFilter, property: String)             extends PropertyFilter
  final case class Number(number: NumberPropertyFilter, property: String)               extends PropertyFilter
  final case class Checkbox(checkbox: CheckboxPropertyFilter, property: String)         extends PropertyFilter
  final case class Select(select: SelectPropertyFilter, property: String)               extends PropertyFilter
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

  final case class Equals(equals: String)             extends TextPropertyFilter with SelectPropertyFilter with DatePropertyFilter
  final case class DoesNotEqual(doesNotEqual: String) extends TextPropertyFilter with SelectPropertyFilter
  final case class Contains(contains: String) extends TextPropertyFilter with MultiSelectPropertyFilter with PeoplePropertyFilter with RelationPropertyFilter
  final case class DoesNotContain(doesNotContain: String)
      extends TextPropertyFilter
      with MultiSelectPropertyFilter
      with PeoplePropertyFilter
      with RelationPropertyFilter
  final case class StartsWith(startsWith: String) extends TextPropertyFilter
  final case class endsWith(endsWith: String)     extends TextPropertyFilter

  final case class NumberEquals(equals: Double)                       extends NumberPropertyFilter
  final case class NumberDoesNotEqual(doesNotEqual: Double)           extends NumberPropertyFilter
  final case class GreaterThan(greaterThan: Double)                   extends NumberPropertyFilter
  final case class LessThan(lessThan: Double)                         extends NumberPropertyFilter
  final case class GreaterThanOrEqualTo(greaterThanOrEqualTo: Double) extends NumberPropertyFilter
  final case class LessThanOrEqualTo(lessThanOrEqualTo: Double)       extends NumberPropertyFilter

  final case class CheckboxEquals(equals: Boolean)             extends CheckboxPropertyFilter
  final case class CheckboxDoesNotEqual(doesNotEqual: Boolean) extends CheckboxPropertyFilter

  final case class Before(before: String)         extends DatePropertyFilter
  final case class After(after: String)           extends DatePropertyFilter
  final case class OnOrBefore(onOrBefore: String) extends DatePropertyFilter
  final case class OnOrAfter(onOrAfter: String)   extends DatePropertyFilter
  final case object PastWeek                      extends DatePropertyFilter
  final case object PastMonth                     extends DatePropertyFilter
  final case object NextWeek                      extends DatePropertyFilter
  final case object NextMonth                     extends DatePropertyFilter
  final case object NextYear                      extends DatePropertyFilter

  final case class StringFormula(string: TextPropertyFilter)         extends FormulaPropertyFilter
  final case class CheckboxFormula(checkbox: CheckboxPropertyFilter) extends FormulaPropertyFilter
  final case class numberFormula(number: NumberPropertyFilter)       extends FormulaPropertyFilter
  final case class DateFormula(date: DatePropertyFilter)             extends FormulaPropertyFilter

  final case class RichtextRollupSubfilter(richText: TextPropertyFilter)              extends RollupSubFilterPropertyFilter
  final case class NumberRollupSubfilter(number: NumberPropertyFilter)                extends RollupSubFilterPropertyFilter
  final case class CheckboxRollupSubfilter(checkbox: CheckboxPropertyFilter)          extends RollupSubFilterPropertyFilter
  final case class SelectRollupSubfilter(select: SelectPropertyFilter)                extends RollupSubFilterPropertyFilter
  final case class MultiSelectRollupSubfilter(multiSelect: MultiSelectPropertyFilter) extends RollupSubFilterPropertyFilter
  final case class RelationRollupSubfilter(relation: RelationPropertyFilter)          extends RollupSubFilterPropertyFilter
  final case class DateRollupSubfilter(date: DatePropertyFilter)                      extends RollupSubFilterPropertyFilter
  final case class PeopleRollupSubfilter(people: PeoplePropertyFilter)                extends RollupSubFilterPropertyFilter
  final case class FilesRollupSubfilter(files: ExistencePropertyFilter)               extends RollupSubFilterPropertyFilter

  final case class Any(any: RollupSubFilterPropertyFilter)     extends RollupPropertyFilter
  final case class None(none: RollupSubFilterPropertyFilter)   extends RollupPropertyFilter
  final case class Every(every: RollupSubFilterPropertyFilter) extends RollupPropertyFilter
  final case class date(date: DatePropertyFilter)              extends RollupPropertyFilter
  final case class NumberRollup(number: NumberPropertyFilter)  extends RollupPropertyFilter

}
