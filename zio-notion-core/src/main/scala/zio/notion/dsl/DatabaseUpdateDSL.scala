package zio.notion.dsl

import zio.notion.model.common.enumeration.Color
import zio.notion.model.database.description.NumberDescription.NumberFormat
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType.SelectOption

object DatabaseUpdateDSL {
  implicit def selectOptionConversion(string: String): SelectOption = SelectOption(string, None)

  implicit class stringOps(string: String) {
    def gray: SelectOption   = SelectOption(string, Some(Color.Gray))
    def brown: SelectOption  = SelectOption(string, Some(Color.Brown))
    def orange: SelectOption = SelectOption(string, Some(Color.Orange))
    def yellow: SelectOption = SelectOption(string, Some(Color.Yellow))
    def green: SelectOption  = SelectOption(string, Some(Color.Green))
    def blue: SelectOption   = SelectOption(string, Some(Color.Blue))
    def purple: SelectOption = SelectOption(string, Some(Color.Purple))
    def pink: SelectOption   = SelectOption(string, Some(Color.Pink))
    def red: SelectOption    = SelectOption(string, Some(Color.Red))
  }

  val asTitle: PropertyType    = PropertyType.Title
  val asRichText: PropertyType = PropertyType.RichText

  def asNumber(format: NumberFormat): PropertyType.Number = PropertyType.Number(format)
  val asEuro: PropertyType.Number                         = asNumber(NumberFormat.Euro)
  val asDollar: PropertyType.Number                       = asNumber(NumberFormat.Dollar)
  val asPound: PropertyType.Number                        = asNumber(NumberFormat.Pound)
  val asPercent: PropertyType.Number                      = asNumber(NumberFormat.Percent)

  def asSelect(options: SelectOption*): PropertyType.Select           = PropertyType.Select(options)
  def asMultiSelect(options: SelectOption*): PropertyType.MultiSelect = PropertyType.MultiSelect(options)

  def asDate: PropertyType        = PropertyType.Date
  def asPeople: PropertyType      = PropertyType.People
  def asFiles: PropertyType       = PropertyType.Files
  def asCheckbox: PropertyType    = PropertyType.Checkbox
  def asUrl: PropertyType         = PropertyType.Url
  def asEmail: PropertyType       = PropertyType.Email
  def asPhoneNumber: PropertyType = PropertyType.PhoneNumber

  def asFormulaOf(expression: String): PropertyType.Formula     = PropertyType.Formula(expression)
  def asRelationWith(databaseId: String): PropertyType.Relation = PropertyType.Relation(databaseId)

  def asCreatedTime: PropertyType    = PropertyType.CreatedTime
  def asCreatedBy: PropertyType      = PropertyType.CreatedBy
  def asLastEditedTime: PropertyType = PropertyType.LastEditedTime
  def asLastEditedBy: PropertyType   = PropertyType.LastEditedBy
}
