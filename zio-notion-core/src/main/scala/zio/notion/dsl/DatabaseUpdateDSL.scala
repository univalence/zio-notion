package zio.notion.dsl

import zio.notion.model.common.enumeration.Color
import zio.notion.model.database.PropertyDefinitionPatch.PropertySchema
import zio.notion.model.database.PropertyDefinitionPatch.PropertySchema.SelectOption
import zio.notion.model.database.metadata.NumberMetadata.NumberFormat

trait DatabaseUpdateDSL {
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

  val title: PropertySchema    = PropertySchema.Title
  val richText: PropertySchema = PropertySchema.RichText

  def number(format: NumberFormat): PropertySchema.Number = PropertySchema.Number(format)
  val euro: PropertySchema.Number                         = number(NumberFormat.Euro)
  val dollar: PropertySchema.Number                       = number(NumberFormat.Dollar)
  val pound: PropertySchema.Number                        = number(NumberFormat.Pound)
  val percent: PropertySchema.Number                      = number(NumberFormat.Percent)

  def select(options: SelectOption*): PropertySchema.Select           = PropertySchema.Select(options)
  def multiSelect(options: SelectOption*): PropertySchema.MultiSelect = PropertySchema.MultiSelect(options)

  def date: PropertySchema        = PropertySchema.Date
  def people: PropertySchema      = PropertySchema.People
  def files: PropertySchema       = PropertySchema.Files
  def checkbox: PropertySchema    = PropertySchema.Checkbox
  def url: PropertySchema         = PropertySchema.Url
  def email: PropertySchema       = PropertySchema.Email
  def phoneNumber: PropertySchema = PropertySchema.PhoneNumber

  def formulaOf(expression: String): PropertySchema.Formula     = PropertySchema.Formula(expression)
  def relationWith(databaseId: String): PropertySchema.Relation = PropertySchema.Relation(databaseId)

  def createdTime: PropertySchema    = PropertySchema.CreatedTime
  def createdBy: PropertySchema      = PropertySchema.CreatedBy
  def lastEditedTime: PropertySchema = PropertySchema.LastEditedTime
  def lastEditedBy: PropertySchema   = PropertySchema.LastEditedBy
}

object DatabaseUpdateDSL extends DatabaseUpdateDSL
