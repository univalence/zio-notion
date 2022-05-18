package zio.notion.dsl

import zio.notion.model.common.enumeration.Color
import zio.notion.model.database.description.NumberDescription.NumberFormat
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType.SelectOption

object DatabasePatchDSL {
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

  val title: PropertyType    = PropertyType.Title
  val richText: PropertyType = PropertyType.RichText

  def number(format: NumberFormat): PropertyType.Number = PropertyType.Number(format)
  val euro: PropertyType.Number                         = number(NumberFormat.Euro)
  val dollar: PropertyType.Number                       = number(NumberFormat.Dollar)
  val pound: PropertyType.Number                        = number(NumberFormat.Pound)
  val percent: PropertyType.Number                      = number(NumberFormat.Percent)

  def select(options: SelectOption*): PropertyType.Select           = PropertyType.Select(options)
  def multiSelect(options: SelectOption*): PropertyType.MultiSelect = PropertyType.MultiSelect(options)

  def date: PropertyType        = PropertyType.Date
  def people: PropertyType      = PropertyType.People
  def files: PropertyType       = PropertyType.Files
  def checkbox: PropertyType    = PropertyType.Checkbox
  def url: PropertyType         = PropertyType.Url
  def email: PropertyType       = PropertyType.Email
  def phoneNumber: PropertyType = PropertyType.PhoneNumber

  def formulaOf(expression: String): PropertyType.Formula     = PropertyType.Formula(expression)
  def relationWith(databaseId: String): PropertyType.Relation = PropertyType.Relation(databaseId)

  def createdTime: PropertyType    = PropertyType.CreatedTime
  def createdBy: PropertyType      = PropertyType.CreatedBy
  def lastEditedTime: PropertyType = PropertyType.LastEditedTime
  def lastEditedBy: PropertyType   = PropertyType.LastEditedBy
}
