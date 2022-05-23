package zio.notion.dsl

import zio.Clock
import zio.notion.PropertyUpdater._
import zio.notion.model.common.UserId
import zio.notion.model.common.enumeration.Color
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.page.patch.PatchedProperty._
import zio.notion.model.page.property.Link

import java.time.LocalDate

object PatchedColumn {
  final case class PatchedColumnTitle(matcher: ColumnMatcher) {
    def set(title: Seq[RichTextData.Text]): FieldSetter[PatchedTitle] = FieldSetter(matcher, PatchedTitle(title))
    def set(title: String): FieldSetter[PatchedTitle]                 = set(Seq(RichTextData.default(title, Annotations.default)))
    def update[E](f: Seq[RichTextData] => Seq[RichTextData]): FieldUpdater[E, PatchedTitle] =
      FieldUpdater.succeed(matcher, property => property.copy(title = f(property.title)))

    def capitalize: UFieldUpdater[PatchedTitle] =
      update(_.map {
        case d: RichTextData.Text => d.copy(text = d.text.copy(content = d.text.content.capitalize), plainText = d.plainText.capitalize)
        case d                    => d
      })
  }

  final case class PatchedColumnRichText(matcher: ColumnMatcher) {
    def set(title: Seq[RichTextData.Text]): FieldSetter[PatchedRichText] = FieldSetter(matcher, PatchedRichText(title))
    def set(title: String): FieldSetter[PatchedRichText]                 = set(Seq(RichTextData.default(title, Annotations.default)))
    def update[E](f: Seq[RichTextData] => Seq[RichTextData]): FieldUpdater[E, PatchedRichText] =
      FieldUpdater.succeed(matcher, property => property.copy(richText = f(property.richText)))

    def write(text: String, annotations: Annotations = Annotations.default): FieldSetter[PatchedRichText] =
      set(List(RichTextData.default(text, annotations)))
    def annotate(f: Annotations => Annotations): UFieldUpdater[PatchedRichText] =
      update(_.map {
        case d: RichTextData.Text     => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Mention  => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Equation => d.copy(annotations = f(d.annotations))
      })

    def reset: UFieldUpdater[PatchedRichText]               = annotate(_ => Annotations.default)
    def bold: UFieldUpdater[PatchedRichText]                = annotate(_.copy(bold = true))
    def italic: UFieldUpdater[PatchedRichText]              = annotate(_.copy(italic = true))
    def strikethrough: UFieldUpdater[PatchedRichText]       = annotate(_.copy(strikethrough = true))
    def underline: UFieldUpdater[PatchedRichText]           = annotate(_.copy(underline = true))
    def code: UFieldUpdater[PatchedRichText]                = annotate(_.copy(code = true))
    def color(color: Color): UFieldUpdater[PatchedRichText] = annotate(_.copy(color = color))
  }

  final case class PatchedColumnNumber(matcher: ColumnMatcher) {
    def set(number: Double): FieldSetter[PatchedNumber]           = FieldSetter(matcher, PatchedNumber(number))
    def update(f: Double => Double): UFieldUpdater[PatchedNumber] = maybeUpdate(p => Right(f(p)))
    def maybeUpdate[E](f: Double => Either[E, Double]): FieldUpdater[E, PatchedNumber] =
      FieldUpdater.apply(matcher, property => f(property.number).map(number => property.copy(number = number)))

    def divide(number: Double): UFieldUpdater[PatchedNumber] = update(_ / number) // TODO: Transform to maybe update
    def add(number: Double): UFieldUpdater[PatchedNumber]    = update(_ + number)
    def minus(number: Double): UFieldUpdater[PatchedNumber]  = update(_ - number)
    def times(number: Double): UFieldUpdater[PatchedNumber]  = update(_ * number)
    def pow(number: Double): UFieldUpdater[PatchedNumber]    = update(Math.pow(_, number))
    def floor: UFieldUpdater[PatchedNumber]                  = update(Math.floor)
    def ceil: UFieldUpdater[PatchedNumber]                   = update(Math.ceil)
  }

  final case class PatchedColumnCheckbox(matcher: ColumnMatcher) {
    def set(checkbox: Boolean): FieldSetter[PatchedCheckbox] = FieldSetter(matcher, PatchedCheckbox(checkbox))
    def update(f: Boolean => Boolean): UFieldUpdater[PatchedCheckbox] =
      FieldUpdater.succeed(matcher, property => property.copy(checkbox = f(property.checkbox)))

    def check: FieldSetter[PatchedCheckbox]     = set(true)
    def uncheck: FieldSetter[PatchedCheckbox]   = set(false)
    def reverse: UFieldUpdater[PatchedCheckbox] = update(!_)
  }

  final case class PatchedColumnSelect(matcher: ColumnMatcher) {
    def set(id: Option[String], name: Option[String]): FieldSetter[PatchedSelect] = FieldSetter(matcher, PatchedSelect(id, name))

    def setUsingId(id: String): FieldSetter[PatchedSelect]     = set(Some(id), None)
    def setUsingName(name: String): FieldSetter[PatchedSelect] = set(None, Some(name))
  }

  final case class PatchedColumnMultiSelect(matcher: ColumnMatcher) {
    def set(selects: List[PatchedSelect]): FieldSetter[PatchedMultiSelect] = FieldSetter(matcher, PatchedMultiSelect(selects))
    def update(f: List[PatchedSelect] => List[PatchedSelect]): UFieldUpdater[PatchedMultiSelect] =
      FieldUpdater.succeed(matcher, property => property.copy(multiSelect = f(property.multiSelect)))

    def removeUsingIdIfExists(id: String): UFieldUpdater[PatchedMultiSelect]     = update(_.filterNot(_.id.contains(id)))
    def removeUsingNameIfExists(name: String): UFieldUpdater[PatchedMultiSelect] = update(_.filterNot(_.name.contains(name)))
    def addUsingId(id: String): UFieldUpdater[PatchedMultiSelect]                = update(_ :+ PatchedSelect(Some(id), None))
    def addUsingName(name: String): UFieldUpdater[PatchedMultiSelect]            = update(_ :+ PatchedSelect(None, Some(name)))
  }

  final case class PatchedColumnDate(matcher: ColumnMatcher) {
    def set(start: LocalDate, end: Option[LocalDate], timeZone: Option[String]): FieldSetter[PatchedDate] =
      FieldSetter(matcher, PatchedDate(start, end, timeZone))

    def startAt(date: LocalDate): FieldSetter[PatchedDate] = set(date, None, None)
    def endAt(f: LocalDate => LocalDate): UFieldUpdater[PatchedDate] =
      FieldUpdater.succeed(matcher, property => property.copy(end = Some(f(property.start))))
    def endAt(date: LocalDate): UFieldUpdater[PatchedDate]                  = endAt(_ => date)
    def between(start: LocalDate, end: LocalDate): FieldSetter[PatchedDate] = set(start, Some(end), None)
    def today: zio.UIO[FieldSetter[PatchedDate]]                            = Clock.localDateTime.map(_.toLocalDate).map(startAt)
  }

  final case class PatchedColumnPeople(matcher: ColumnMatcher) {
    def set(people: Seq[UserId]): FieldSetter[PatchedPeople] = FieldSetter(matcher, PatchedPeople(people))
    def update(f: Seq[UserId] => Seq[UserId]): UFieldUpdater[PatchedPeople] =
      FieldUpdater.succeed(matcher, property => property.copy(people = f(property.people)))

    def add(people: Seq[UserId]): UFieldUpdater[PatchedPeople] = update(_ ++ people)
    def add(people: UserId): UFieldUpdater[PatchedPeople]      = add(List(people))
  }

  final case class PatchedColumnFiles(matcher: ColumnMatcher) {
    def set(files: Seq[Link]): FieldSetter[PatchedFiles] = FieldSetter(matcher, PatchedFiles(files))
    def update(f: Seq[Link] => Seq[Link]): UFieldUpdater[PatchedFiles] =
      FieldUpdater.succeed(matcher, property => property.copy(files = f(property.files)))

    def add(files: Seq[Link]): UFieldUpdater[PatchedFiles]              = update(_ ++ files)
    def add(file: Link): UFieldUpdater[PatchedFiles]                    = add(List(file))
    def filter(predicate: Link => Boolean): UFieldUpdater[PatchedFiles] = update(_.filter(predicate))
  }

  final case class PatchedColumnUrl(matcher: ColumnMatcher) {
    def set(url: String): FieldSetter[PatchedUrl] = FieldSetter(matcher, PatchedUrl(url))
  }

  final case class PatchedColumnEmail(matcher: ColumnMatcher) {
    def set(email: String): FieldSetter[PatchedEmail] = FieldSetter(matcher, PatchedEmail(email))
    def update(f: String => String): UFieldUpdater[PatchedEmail] =
      FieldUpdater.succeed(matcher, property => property.copy(email = f(property.email)))
  }

  final case class PatchedColumnPhoneNumber(matcher: ColumnMatcher) {
    def set(phoneNumber: String): FieldSetter[PatchedPhoneNumber] = FieldSetter(matcher, PatchedPhoneNumber(phoneNumber))
    def update(f: String => String): UFieldUpdater[PatchedPhoneNumber] =
      FieldUpdater.succeed(matcher, property => property.copy(phoneNumber = f(property.phoneNumber)))
  }
}
