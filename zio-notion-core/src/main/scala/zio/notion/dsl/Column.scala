package zio.notion.dsl

import zio.notion.dsl.DatabaseQueryDSL._
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting.Property

final case class Column(name: String) {
  // sorts
  def ascending: Sorting  = Property(name, ascending = true)
  def descending: Sorting = Property(name, ascending = false)

  // filters
  def asNumber: NumberFilterConstructor                 = NumberFilterConstructor(name)
  def asTitle: TitleFilterConstructor                   = TitleFilterConstructor(name)
  def asRichText: RichTextFilterConstructor             = RichTextFilterConstructor(name)
  def asCheckbox: CheckboxFilterConstructor             = CheckboxFilterConstructor(name)
  def asSelect: SelectFilterConstructor                 = SelectFilterConstructor(name)
  def asMultiSelect: MultiSelectFilterConstructor       = MultiSelectFilterConstructor(name)
  def asDate: DateFilterConstructor                     = DateFilterConstructor(name)
  def asPeople: PeopleConstructor                       = PeopleConstructor(name)
  def asFiles: FilesConstructor                         = FilesConstructor(name)
  def asUrl: UrlFilterConstructor                       = UrlFilterConstructor(name)
  def asEmail: EmailFilterConstructor                   = EmailFilterConstructor(name)
  def asPhoneNumber: PhoneNumberFilterConstructor       = PhoneNumberFilterConstructor(name)
  def asRelation: RelationFilterConstructor             = RelationFilterConstructor(name)
  def asCreatedBy: CreatedByConstructor                 = CreatedByConstructor(name)
  def asLastEditedBy: LastEditedByConstructor           = LastEditedByConstructor(name)
  def asCreatedTime: CreatedTimeFilterConstructor       = CreatedTimeFilterConstructor(name)
  def asLastEditedTime: LastEditedTimeFilterConstructor = LastEditedTimeFilterConstructor(name)

}
