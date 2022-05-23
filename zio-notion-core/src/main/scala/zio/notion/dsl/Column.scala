package zio.notion.dsl

import zio.notion.dsl.DatabaseQueryDSL._
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting.Property

final case class Column(colName: String) {
  // sorts
  def ascending: Sorting = Property(colName, ascending = true)

  def descending: Sorting = Property(colName, ascending = false)

  def definition: ColumnDefinition = colDefinition(colName)

  // filters
  def asNumber: NumberFilterConstructor = NumberFilterConstructor(colName)

  def asTitle: TitleFilterConstructor = TitleFilterConstructor(colName)

  def asRichText: RichTextFilterConstructor = RichTextFilterConstructor(colName)

  def asCheckbox: CheckboxFilterConstructor = CheckboxFilterConstructor(colName)

  def asSelect: SelectFilterConstructor = SelectFilterConstructor(colName)

  def asMultiSelect: MultiSelectFilterConstructor = MultiSelectFilterConstructor(colName)

  def asDate: DateFilterConstructor = DateFilterConstructor(colName)

  def asPeople: PeopleConstructor = PeopleConstructor(colName)

  def asFiles: FilesConstructor = FilesConstructor(colName)

  def asUrl: UrlFilterConstructor = UrlFilterConstructor(colName)

  def asEmail: EmailFilterConstructor = EmailFilterConstructor(colName)

  def asPhoneNumber: PhoneNumberFilterConstructor = PhoneNumberFilterConstructor(colName)

  def asRelation: RelationFilterConstructor = RelationFilterConstructor(colName)

  def asCreatedBy: CreatedByConstructor = CreatedByConstructor(colName)

  def asLastEditedBy: LastEditedByConstructor = LastEditedByConstructor(colName)

  def asCreatedTime: CreatedTimeFilterConstructor = CreatedTimeFilterConstructor(colName)

  def asLastEditedTime: LastEditedTimeFilterConstructor = LastEditedTimeFilterConstructor(colName)
}

final case class Columns(predicate: String => Boolean) {
  def definition: ColumnDefinitions = columnDefinitionsMatching(predicate)
}
