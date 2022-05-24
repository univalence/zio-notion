package zio.notion.dsl

import zio.notion.PropertyUpdater.ColumnMatcher.Predicate
import zio.notion.dsl.Columns._
import zio.notion.dsl.PatchedColumn._

final case class Columns(predicate: String => Boolean) {
  def definition: ColumnDefinitions = columnDefinitionsMatching(predicate)

  def asNumber: NumberDSLConstructor = NumberDSLConstructor(predicate)

  def asTitle: TitleDSLConstructor = TitleDSLConstructor(predicate)

  def asRichText: RichTextDSLConstructor = RichTextDSLConstructor(predicate)

  def asCheckbox: CheckboxDSLConstructor = CheckboxDSLConstructor(predicate)

  def asSelect: SelectDSLConstructor = SelectDSLConstructor(predicate)

  def asMultiSelect: MultiSelectDSLConstructor = MultiSelectDSLConstructor(predicate)

  def asDate: DateDSLConstructor = DateDSLConstructor(predicate)

  def asPeople: PeopleDSLConstructor = PeopleDSLConstructor(predicate)

  def asFiles: FilesDSLConstructor = FilesDSLConstructor(predicate)

  def asUrl: UrlDSLConstructor = UrlDSLConstructor(predicate)

  def asEmail: EmailDSLConstructor = EmailDSLConstructor(predicate)

  def asPhoneNumber: PhoneNumberDSLConstructor = PhoneNumberDSLConstructor(predicate)
}

object Columns {

  final case class TitleDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnTitle = PatchedColumnTitle(Predicate(predicate))
  }

  final case class RichTextDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnRichText = PatchedColumnRichText(Predicate(predicate))
  }

  final case class NumberDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnNumber = PatchedColumnNumber(Predicate(predicate))
  }

  final case class CheckboxDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnCheckbox = PatchedColumnCheckbox(Predicate(predicate))
  }

  final case class SelectDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnSelect = PatchedColumnSelect(Predicate(predicate))
  }

  final case class MultiSelectDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnMultiSelect = PatchedColumnMultiSelect(Predicate(predicate))
  }

  final case class DateDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnDate = PatchedColumnDate(Predicate(predicate))
  }

  final case class PeopleDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnPeople = PatchedColumnPeople(Predicate(predicate))
  }

  final case class FilesDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnFiles = PatchedColumnFiles(Predicate(predicate))
  }

  final case class UrlDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnUrl = PatchedColumnUrl(Predicate(predicate))
  }

  final case class EmailDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnEmail = PatchedColumnEmail(Predicate(predicate))
  }

  final case class PhoneNumberDSLConstructor private (predicate: String => Boolean) {
    def patch: PatchedColumnPhoneNumber = PatchedColumnPhoneNumber(Predicate(predicate))
  }
}
