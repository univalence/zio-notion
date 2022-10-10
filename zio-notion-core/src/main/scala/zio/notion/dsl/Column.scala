package zio.notion.dsl

import zio.notion.dsl.Column._
import zio.notion.dsl.PatchedColumn._
import zio.notion.model.database.query.PropertyFilter
import zio.notion.model.database.query.PropertyFilter._
import zio.notion.model.database.query.PropertyFilter.DatePropertyFilter.{
  After,
  Before,
  NextMonth,
  NextWeek,
  NextYear,
  OnOrAfter,
  OnOrBefore,
  PastMonth,
  PastWeek
}
import zio.notion.model.database.query.PropertyFilter.NumberPropertyFilter.{GreaterThan, GreaterThanOrEqualTo, LessThan, LessThanOrEqualTo}
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.{EndsWith, StartsWith}
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting.Property

import java.time.{LocalDate, OffsetDateTime}

final case class Column(colName: String) {
  def definition: ColumnDefinition = colDefinition(colName)

  // sorts
  def ascending: Sorting  = Property(colName, ascending = true)
  def descending: Sorting = Property(colName, ascending = false)

  // filters
  def asNumber: NumberDSLConstructor                 = NumberDSLConstructor(colName)
  def asTitle: TitleDSLConstructor                   = TitleDSLConstructor(colName)
  def asRichText: RichTextDSLConstructor             = RichTextDSLConstructor(colName)
  def asCheckbox: CheckboxDSLConstructor             = CheckboxDSLConstructor(colName)
  def asSelect: SelectDSLConstructor                 = SelectDSLConstructor(colName)
  def asStatus: SelectDSLConstructor                 = SelectDSLConstructor(colName)
  def asMultiSelect: MultiSelectDSLConstructor       = MultiSelectDSLConstructor(colName)
  def asDate: DateDSLConstructor                     = DateDSLConstructor(colName)
  def asDateTime: DateTimeDSLConstructor             = DateTimeDSLConstructor(colName)
  def asPeople: PeopleDSLConstructor                 = PeopleDSLConstructor(colName)
  def asFiles: FilesDSLConstructor                   = FilesDSLConstructor(colName)
  def asUrl: UrlDSLConstructor                       = UrlDSLConstructor(colName)
  def asEmail: EmailDSLConstructor                   = EmailDSLConstructor(colName)
  def asPhoneNumber: PhoneNumberDSLConstructor       = PhoneNumberDSLConstructor(colName)
  def asRelation: RelationDSLConstructor             = RelationDSLConstructor(colName)
  def asCreatedBy: CreatedByDSLConstructor           = CreatedByDSLConstructor(colName)
  def asLastEditedBy: LastEditedByDSLConstructor     = LastEditedByDSLConstructor(colName)
  def asCreatedTime: CreatedTimeDSLConstructor       = CreatedTimeDSLConstructor(colName)
  def asLastEditedTime: LastEditedTimeDSLConstructor = LastEditedTimeDSLConstructor(colName)

  // TODO Formula DSL

  // TODO Rollup DSL
}

object Column {

  final case class TitleDSLConstructor private (property: String) {
    def startsWith(string: String): Title     = Title(property, StartsWith(string))
    def endsWith(string: String): Title       = Title(property, EndsWith(string))
    def equals(string: String): Title         = Title(property, Equals(string))
    def doesNotEqual(string: String): Title   = Title(property, DoesNotEqual(string))
    def contains(string: String): Title       = Title(property, Contains(string))
    def doesNotContain(string: String): Title = Title(property, DoesNotContain(string))
    def isEmpty: Title                        = Title(property, IsEmpty(true))
    def isNotEmpty: Title                     = Title(property, IsNotEmpty(true))

    def patch: PatchedColumnTitle = PatchedColumnTitle(property)
  }

  final case class RichTextDSLConstructor private (property: String) {
    def startsWith(string: String): RichText     = RichText(property, StartsWith(string))
    def endsWith(string: String): RichText       = RichText(property, EndsWith(string))
    def equals(string: String): RichText         = RichText(property, Equals(string))
    def doesNotEqual(string: String): RichText   = RichText(property, DoesNotEqual(string))
    def contains(string: String): RichText       = RichText(property, Contains(string))
    def doesNotContain(string: String): RichText = RichText(property, DoesNotContain(string))
    def isEmpty: RichText                        = RichText(property, IsEmpty(true))
    def isNotEmpty: RichText                     = RichText(property, IsNotEmpty(true))

    def patch: PatchedColumnRichText = PatchedColumnRichText(property)
  }

  final case class NumberDSLConstructor private (property: String) {
    def equals(double: Double): Number               = Number(property, NumberPropertyFilter.Equals(double))
    def doesNotEqual(double: Double): Number         = Number(property, NumberPropertyFilter.DoesNotEqual(double))
    def greaterThan(double: Double): Number          = Number(property, GreaterThan(double))
    def lessThan(double: Double): Number             = Number(property, LessThan(double))
    def greaterThanOrEqualTo(double: Double): Number = Number(property, GreaterThanOrEqualTo(double))
    def lessThanOrEqualTo(double: Double): Number    = Number(property, LessThanOrEqualTo(double))
    def isEmpty: Number                              = Number(property, IsEmpty(true))
    def isNotEmpty: Number                           = Number(property, IsNotEmpty(true))

    def ==(double: Double): Number = equals(double)
    def !=(double: Double): Number = doesNotEqual(double)
    def >(double: Double): Number  = greaterThan(double)
    def <(double: Double): Number  = lessThan(double)
    def >=(double: Double): Number = greaterThanOrEqualTo(double)
    def <=(double: Double): Number = lessThanOrEqualTo(double)

    def patch: PatchedColumnNumber = PatchedColumnNumber(property)
  }

  final case class CheckboxDSLConstructor private (property: String) {
    def equals(boolean: Boolean): Checkbox       = Checkbox(property, CheckboxPropertyFilter.Equals(boolean))
    def doesNotEqual(boolean: Boolean): Checkbox = Checkbox(property, CheckboxPropertyFilter.DoesNotEqual(boolean))

    def isTrue: Checkbox  = equals(true)
    def isFalse: Checkbox = equals(false)

    def patch: PatchedColumnCheckbox = PatchedColumnCheckbox(property)
  }

  final case class SelectDSLConstructor private (property: String) {
    def equals(string: String): Select       = Select(property, Equals(string))
    def doesNotEqual(string: String): Select = Select(property, DoesNotEqual(string))
    def isEmpty: Select                      = Select(property, IsEmpty(true))
    def isNotEmpty: Select                   = Select(property, IsNotEmpty(true))

    def patch: PatchedColumnSelect = PatchedColumnSelect(property)
  }

  final case class MultiSelectDSLConstructor private (property: String) {
    def equals(string: String): MultiSelect         = MultiSelect(property, Equals(string))
    def doesNotEqual(string: String): MultiSelect   = MultiSelect(property, DoesNotEqual(string))
    def contains(string: String): MultiSelect       = MultiSelect(property, Contains(string))
    def doesNotContain(string: String): MultiSelect = MultiSelect(property, DoesNotContain(string))
    def isEmpty: MultiSelect                        = MultiSelect(property, IsEmpty(true))
    def isNotEmpty: MultiSelect                     = MultiSelect(property, IsNotEmpty(true))

    def patch: PatchedColumnMultiSelect = PatchedColumnMultiSelect(property)
  }

  final case class DateDSLConstructor private (property: String) {
    def equals(date: LocalDate): Date     = Date(property, Equals(date.toString))
    def before(date: LocalDate): Date     = Date(property, Before(date.toString))
    def after(date: LocalDate): Date      = Date(property, After(date.toString))
    def onOrBefore(date: LocalDate): Date = Date(property, OnOrBefore(date.toString))
    def onOrAfter(date: LocalDate): Date  = Date(property, OnOrAfter(date.toString))
    def pastWeek: Date                    = Date(property, PastWeek)
    def pastMonth: Date                   = Date(property, PastMonth)
    def nextWeek: Date                    = Date(property, NextWeek)
    def nextMonth: Date                   = Date(property, NextMonth)
    def nextYear: Date                    = Date(property, NextYear)
    def isEmpty: Date                     = Date(property, IsEmpty(true))
    def isNotEmpty: Date                  = Date(property, IsNotEmpty(true))

    def >(date: LocalDate): Date  = after(date)
    def <(date: LocalDate): Date  = before(date)
    def >=(date: LocalDate): Date = onOrAfter(date)
    def <=(date: LocalDate): Date = onOrBefore(date)

    def patch: PatchedColumnDate = PatchedColumnDate(property)
  }

  final case class DateTimeDSLConstructor private (property: String) {
    def equals(date: OffsetDateTime): Date     = Date(property, Equals(date.toString))
    def before(date: OffsetDateTime): Date     = Date(property, Before(date.toString))
    def after(date: OffsetDateTime): Date      = Date(property, After(date.toString))
    def onOrBefore(date: OffsetDateTime): Date = Date(property, OnOrBefore(date.toString))
    def onOrAfter(date: OffsetDateTime): Date  = Date(property, OnOrAfter(date.toString))
    def pastWeek: Date                         = Date(property, PastWeek)
    def pastMonth: Date                        = Date(property, PastMonth)
    def nextWeek: Date                         = Date(property, NextWeek)
    def nextMonth: Date                        = Date(property, NextMonth)
    def nextYear: Date                         = Date(property, NextYear)
    def isEmpty: Date                          = Date(property, IsEmpty(true))
    def isNotEmpty: Date                       = Date(property, IsNotEmpty(true))

    def >(date: OffsetDateTime): Date  = after(date)
    def <(date: OffsetDateTime): Date  = before(date)
    def >=(date: OffsetDateTime): Date = onOrAfter(date)
    def <=(date: OffsetDateTime): Date = onOrBefore(date)

    def patch: PatchedColumnDateTime = PatchedColumnDateTime(property)
  }

  final case class PeopleDSLConstructor private (property: String) {
    def contains(string: String): People       = People(property, Contains(string))
    def doesNotContain(string: String): People = People(property, DoesNotContain(string))
    def isEmpty: People                        = People(property, IsEmpty(true))
    def isNotEmpty: People                     = People(property, IsNotEmpty(true))

    def patch: PatchedColumnPeople = PatchedColumnPeople(property)
  }

  final case class FilesDSLConstructor private (property: String) {
    def isEmpty: Files    = Files(property, IsEmpty(true))
    def isNotEmpty: Files = Files(property, IsNotEmpty(true))

    def patch: PatchedColumnFiles = PatchedColumnFiles(property)
  }

  final case class UrlDSLConstructor private (property: String) {
    def equals(string: String): Url         = Url(property, Equals(string))
    def doesNotEqual(string: String): Url   = Url(property, DoesNotEqual(string))
    def contains(string: String): Url       = Url(property, Contains(string))
    def doesNotContain(string: String): Url = Url(property, DoesNotContain(string))
    def isEmpty: Url                        = Url(property, IsEmpty(true))
    def isNotEmpty: Url                     = Url(property, IsNotEmpty(true))

    def patch: PatchedColumnUrl = PatchedColumnUrl(property)
  }

  final case class EmailDSLConstructor private (property: String) {
    def equals(string: String): Email         = Email(property, Equals(string))
    def doesNotEqual(string: String): Email   = Email(property, DoesNotEqual(string))
    def contains(string: String): Email       = Email(property, Contains(string))
    def doesNotContain(string: String): Email = Email(property, DoesNotContain(string))
    def isEmpty: Email                        = Email(property, IsEmpty(true))
    def isNotEmpty: Email                     = Email(property, IsNotEmpty(true))

    def patch: PatchedColumnEmail = PatchedColumnEmail(property)
  }

  final case class PhoneNumberDSLConstructor private (property: String) {
    def equals(string: String): PhoneNumber         = PhoneNumber(property, Equals(string))
    def doesNotEqual(string: String): PhoneNumber   = PhoneNumber(property, DoesNotEqual(string))
    def contains(string: String): PhoneNumber       = PhoneNumber(property, Contains(string))
    def doesNotContain(string: String): PhoneNumber = PhoneNumber(property, DoesNotContain(string))
    def isEmpty: PhoneNumber                        = PhoneNumber(property, IsEmpty(true))
    def isNotEmpty: PhoneNumber                     = PhoneNumber(property, IsNotEmpty(true))

    def patch: PatchedColumnPhoneNumber = PatchedColumnPhoneNumber(property)
  }

  final case class RelationDSLConstructor private (property: String) {
    def contains(string: String): Relation       = Relation(property, Contains(string))
    def doesNotContain(string: String): Relation = Relation(property, DoesNotContain(string))
    def isEmpty: Relation                        = Relation(property, IsEmpty(true))
    def isNotEmpty: Relation                     = Relation(property, IsNotEmpty(true))

    def patch: PatchedColumnRelation = PatchedColumnRelation(property)
  }

  final case class CreatedByDSLConstructor private (property: String) {
    def contains(string: String): CreatedBy       = CreatedBy(property, Contains(string))
    def doesNotContain(string: String): CreatedBy = CreatedBy(property, DoesNotContain(string))
    def isEmpty: CreatedBy                        = CreatedBy(property, IsEmpty(true))
    def isNotEmpty: CreatedBy                     = CreatedBy(property, IsNotEmpty(true))
  }

  final case class LastEditedByDSLConstructor private (property: String) {
    def contains(string: String): LastEditedBy       = LastEditedBy(property, Contains(string))
    def doesNotContain(string: String): LastEditedBy = LastEditedBy(property, DoesNotContain(string))
    def isEmpty: LastEditedBy                        = LastEditedBy(property, IsEmpty(true))
    def isNotEmpty: LastEditedBy                     = LastEditedBy(property, IsNotEmpty(true))
  }

  final case class CreatedTimeDSLConstructor private (property: String) {
    def equals(date: LocalDate): CreatedTime     = PropertyFilter.CreatedTime(property, Equals(date.toString))
    def before(date: LocalDate): CreatedTime     = PropertyFilter.CreatedTime(property, Before(date.toString))
    def after(date: LocalDate): CreatedTime      = PropertyFilter.CreatedTime(property, After(date.toString))
    def onOrBefore(date: LocalDate): CreatedTime = PropertyFilter.CreatedTime(property, OnOrBefore(date.toString))
    def onOrAfter(date: LocalDate): CreatedTime  = PropertyFilter.CreatedTime(property, OnOrAfter(date.toString))
    def pastWeek: CreatedTime                    = PropertyFilter.CreatedTime(property, PastWeek)
    def pastMonth: CreatedTime                   = PropertyFilter.CreatedTime(property, PastMonth)
    def nextWeek: CreatedTime                    = PropertyFilter.CreatedTime(property, NextWeek)
    def nextMonth: CreatedTime                   = PropertyFilter.CreatedTime(property, NextMonth)
    def nextYear: CreatedTime                    = PropertyFilter.CreatedTime(property, NextYear)
    def isEmpty: CreatedTime                     = PropertyFilter.CreatedTime(property, IsEmpty(true))
    def isNotEmpty: CreatedTime                  = PropertyFilter.CreatedTime(property, IsNotEmpty(true))

    def >(date: LocalDate): CreatedTime  = after(date)
    def <(date: LocalDate): CreatedTime  = before(date)
    def >=(date: LocalDate): CreatedTime = onOrAfter(date)
    def <=(date: LocalDate): CreatedTime = onOrBefore(date)
  }

  final case class LastEditedTimeDSLConstructor private (property: String) {
    def equals(date: LocalDate): LastEditedTime     = PropertyFilter.LastEditedTime(property, Equals(date.toString))
    def before(date: LocalDate): LastEditedTime     = PropertyFilter.LastEditedTime(property, Before(date.toString))
    def after(date: LocalDate): LastEditedTime      = PropertyFilter.LastEditedTime(property, After(date.toString))
    def onOrBefore(date: LocalDate): LastEditedTime = PropertyFilter.LastEditedTime(property, OnOrBefore(date.toString))
    def onOrAfter(date: LocalDate): LastEditedTime  = PropertyFilter.LastEditedTime(property, OnOrAfter(date.toString))
    def pastWeek: LastEditedTime                    = PropertyFilter.LastEditedTime(property, PastWeek)
    def pastMonth: LastEditedTime                   = PropertyFilter.LastEditedTime(property, PastMonth)
    def nextWeek: LastEditedTime                    = PropertyFilter.LastEditedTime(property, NextWeek)
    def nextMonth: LastEditedTime                   = PropertyFilter.LastEditedTime(property, NextMonth)
    def nextYear: LastEditedTime                    = PropertyFilter.LastEditedTime(property, NextYear)
    def isEmpty: LastEditedTime                     = PropertyFilter.LastEditedTime(property, IsEmpty(true))
    def isNotEmpty: LastEditedTime                  = PropertyFilter.LastEditedTime(property, IsNotEmpty(true))

    def >(date: LocalDate): LastEditedTime  = after(date)
    def <(date: LocalDate): LastEditedTime  = before(date)
    def >=(date: LocalDate): LastEditedTime = onOrAfter(date)
    def <=(date: LocalDate): LastEditedTime = onOrBefore(date)
  }
}
