package zio.notion.dsl

import zio.notion.model.database.query.{Filter, PropertyFilter, Sorts}
import zio.notion.model.database.query.PropertyFilter._
import zio.notion.model.database.query.PropertyFilter.NumberPropertyFilter.{GreaterThan, GreaterThanOrEqualTo, LessThan, LessThanOrEqualTo}
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.{EndsWith, StartsWith}
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting._
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.{CreatedTime, LastEditedTime}

object query {

  // Sort helpers

  implicit class StringOps(string: String) {
    def ascending: Sorting  = Property(string, ascending = true)
    def descending: Sorting = Property(string, ascending = false)
  }

  implicit def timestampTypeToSort(timestampType: TimestampType): Sorts = Timestamp(timestampType, ascending = true)

  implicit def stringToSorting(string: String): Sorts = Property(string, ascending = true)

  implicit def sortingToSort(sorting: Sorting): Sorts = Sorts(List(sorting))

  val createdTime: TimestampType    = CreatedTime
  val lastEditedTime: TimestampType = LastEditedTime

  // Filter helpers

  implicit def propertyFilterToFilter(propertyFilter: PropertyFilter): Filter = Filter.One(propertyFilter)

  final case class TitleFilterConstructor private (property: String) {
    def startsWith(string: String): Title     = Title(property, StartsWith(string))
    def endsWith(string: String): Title       = Title(property, EndsWith(string))
    def equals(string: String): Title         = Title(property, Equals(string))
    def doesNotEqual(string: String): Title   = Title(property, DoesNotEqual(string))
    def contains(string: String): Title       = Title(property, Contains(string))
    def doesNotContain(string: String): Title = Title(property, DoesNotContain(string))
    def isEmpty: Title                        = Title(property, IsEmpty(true))
    def isNotEmpty: Title                     = Title(property, IsNotEmpty(true))
  }

  def title(propertyName: String): TitleFilterConstructor = TitleFilterConstructor(propertyName)

  final case class RichTextFilterConstructor private (property: String) {
    def startsWith(string: String): RichText     = RichText(property, StartsWith(string))
    def endsWith(string: String): RichText       = RichText(property, EndsWith(string))
    def equals(string: String): RichText         = RichText(property, Equals(string))
    def doesNotEqual(string: String): RichText   = RichText(property, DoesNotEqual(string))
    def contains(string: String): RichText       = RichText(property, Contains(string))
    def doesNotContain(string: String): RichText = RichText(property, DoesNotContain(string))
    def isEmpty: RichText                        = RichText(property, IsEmpty(true))
    def isNotEmpty: RichText                     = RichText(property, IsNotEmpty(true))
  }

  def richText(propertyName: String): RichTextFilterConstructor = RichTextFilterConstructor(propertyName)

  final case class NumberFilterConstructor private (property: String) {
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
  }

  def number(propertyName: String): NumberFilterConstructor = NumberFilterConstructor(propertyName)

  final case class CheckboxFilterConstructor private (property: String) {
    def equals(boolean: Boolean): Checkbox       = Checkbox(property, CheckboxPropertyFilter.Equals(boolean))
    def doesNotEqual(boolean: Boolean): Checkbox = Checkbox(property, CheckboxPropertyFilter.DoesNotEqual(boolean))

    def isTrue: Checkbox  = equals(true)
    def isFalse: Checkbox = equals(false)
  }
}
