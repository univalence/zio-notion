package zio.notion.dsl

import zio.Clock
import zio.notion.NotionError
import zio.notion.model.common.Id
import zio.notion.model.common.enumeration.Color
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.page.Page.Patch.Operations.Operation._
import zio.notion.model.page.PatchedProperty._
import zio.notion.model.page.property.Link

import java.time.{LocalDate, LocalDateTime, OffsetDateTime, ZoneOffset}

object PatchedColumn {

  final case class PatchedColumnTitle(columnName: String) extends {
    def set(title: Seq[RichTextData.Text]): SetProperty[PatchedTitle] = SetProperty(columnName, PatchedTitle(title))
    def set(title: String): SetProperty[PatchedTitle]                 = set(Seq(RichTextData.default(title, Annotations.default)))

    def update(f: Seq[RichTextData] => Seq[RichTextData]): UpdateProperty[PatchedTitle] =
      UpdateProperty.succeed(columnName, property => property.copy(title = f(property.title)))

    def capitalize: UpdateProperty[PatchedTitle] =
      update(_.map {
        case d: RichTextData.Text => d.copy(text = d.text.copy(content = d.text.content.capitalize), plainText = d.plainText.capitalize)
        case d                    => d
      })
  }

  final case class PatchedColumnRichText(columnName: String) {
    def set(title: Seq[RichTextData.Text]): SetProperty[PatchedRichText] = SetProperty(columnName, PatchedRichText(title))
    def set(title: String): SetProperty[PatchedRichText]                 = set(Seq(RichTextData.default(title, Annotations.default)))

    def update(f: Seq[RichTextData] => Seq[RichTextData]): UpdateProperty[PatchedRichText] =
      UpdateProperty.succeed(columnName, property => property.copy(richText = f(property.richText)))

    def write(text: String, annotations: Annotations = Annotations.default): SetProperty[PatchedRichText] =
      set(List(RichTextData.default(text, annotations)))

    def annotate(f: Annotations => Annotations): UpdateProperty[PatchedRichText] =
      update(_.map {
        case d: RichTextData.Text     => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Mention  => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Equation => d.copy(annotations = f(d.annotations))
      })

    def reset: UpdateProperty[PatchedRichText]               = annotate(_ => Annotations.default)
    def bold: UpdateProperty[PatchedRichText]                = annotate(_.copy(bold = true))
    def italic: UpdateProperty[PatchedRichText]              = annotate(_.copy(italic = true))
    def strikethrough: UpdateProperty[PatchedRichText]       = annotate(_.copy(strikethrough = true))
    def underline: UpdateProperty[PatchedRichText]           = annotate(_.copy(underline = true))
    def code: UpdateProperty[PatchedRichText]                = annotate(_.copy(code = true))
    def color(color: Color): UpdateProperty[PatchedRichText] = annotate(_.copy(color = color))
  }

  final case class PatchedColumnNumber(columnName: String) {
    def set(number: Double): SetProperty[PatchedNumber]            = SetProperty(columnName, PatchedNumber(number))
    def update(f: Double => Double): UpdateProperty[PatchedNumber] = maybeUpdate(p => Right(f(p)))

    def maybeUpdate(f: Double => Either[NotionError, Double]): UpdateProperty[PatchedNumber] =
      UpdateProperty(columnName, property => f(property.number).map(number => property.copy(number = number)))

    def divide(number: Double): UpdateProperty[PatchedNumber] = update(_ / number) // TODO: Transform to maybe update
    def add(number: Double): UpdateProperty[PatchedNumber]    = update(_ + number)
    def minus(number: Double): UpdateProperty[PatchedNumber]  = update(_ - number)
    def times(number: Double): UpdateProperty[PatchedNumber]  = update(_ * number)
    def pow(number: Double): UpdateProperty[PatchedNumber]    = update(Math.pow(_, number))
    def floor: UpdateProperty[PatchedNumber]                  = update(Math.floor)
    def ceil: UpdateProperty[PatchedNumber]                   = update(Math.ceil)
  }

  final case class PatchedColumnCheckbox(columnName: String) {
    def set(checkbox: Boolean): SetProperty[PatchedCheckbox] = SetProperty(columnName, PatchedCheckbox(checkbox))

    def update(f: Boolean => Boolean): UpdateProperty[PatchedCheckbox] =
      UpdateProperty.succeed(columnName, property => property.copy(checkbox = f(property.checkbox)))

    def check: SetProperty[PatchedCheckbox]      = set(true)
    def uncheck: SetProperty[PatchedCheckbox]    = set(false)
    def reverse: UpdateProperty[PatchedCheckbox] = update(!_)
  }

  final case class PatchedColumnSelect(columnName: String) {
    def set(id: Option[String], name: Option[String]): SetProperty[PatchedSelect] = SetProperty(columnName, PatchedSelect(id, name))

    def setUsingId(id: String): SetProperty[PatchedSelect]     = set(Some(id), None)
    def setUsingName(name: String): SetProperty[PatchedSelect] = set(None, Some(name))
  }

  final case class PatchedColumnMultiSelect(columnName: String) {
    def set(selects: List[PatchedSelect]): SetProperty[PatchedMultiSelect] = SetProperty(columnName, PatchedMultiSelect(selects))

    def update(f: List[PatchedSelect] => List[PatchedSelect]): UpdateProperty[PatchedMultiSelect] =
      UpdateProperty.succeed(columnName, property => property.copy(multiSelect = f(property.multiSelect)))

    def removeUsingIdIfExists(id: String): UpdateProperty[PatchedMultiSelect]     = update(_.filterNot(_.id.contains(id)))
    def removeUsingNameIfExists(name: String): UpdateProperty[PatchedMultiSelect] = update(_.filterNot(_.name.contains(name)))
    def addUsingId(id: String): UpdateProperty[PatchedMultiSelect]                = update(_ :+ PatchedSelect(Some(id), None))
    def addUsingName(name: String): UpdateProperty[PatchedMultiSelect]            = update(_ :+ PatchedSelect(None, Some(name)))
  }

  final case class PatchedColumnDate(columnName: String) {
    private val utc = ZoneOffset.UTC

    def set(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String]): SetProperty[PatchedDate] =
      SetProperty(columnName, PatchedDate(start, end, timeZone))

    def startAt(date: OffsetDateTime): SetProperty[PatchedDate] = set(date, None, None)
    def startAt(date: LocalDateTime): SetProperty[PatchedDate]  = startAt(date.atOffset(utc))
    def startAt(date: LocalDate): SetProperty[PatchedDate]      = startAt(date.atStartOfDay())

    def endAt(f: OffsetDateTime => OffsetDateTime): UpdateProperty[PatchedDate] =
      UpdateProperty.succeed(columnName, property => property.copy(end = Some(f(property.start))))
    def endAt(date: OffsetDateTime): UpdateProperty[PatchedDate] = endAt(_ => date)
    def endAt(date: LocalDateTime): UpdateProperty[PatchedDate]  = endAt(date.atOffset(utc))
    def endAt(date: LocalDate): UpdateProperty[PatchedDate]      = endAt(date.atStartOfDay())

    def between(start: OffsetDateTime, end: OffsetDateTime): SetProperty[PatchedDate] = set(start, Some(end), None)
    def between(start: LocalDateTime, end: LocalDateTime): SetProperty[PatchedDate]   = between(start.atOffset(utc), end.atOffset(utc))
    def between(start: LocalDate, end: LocalDate): SetProperty[PatchedDate]           = between(start.atStartOfDay(), end.atStartOfDay())

    def today: zio.UIO[SetProperty[PatchedDate]] = Clock.currentDateTime.map(startAt)
  }

  final case class PatchedColumnPeople(columnName: String) {
    def set(people: Seq[Id]): SetProperty[PatchedPeople] = SetProperty(columnName, PatchedPeople(people))

    def update(f: Seq[Id] => Seq[Id]): UpdateProperty[PatchedPeople] =
      UpdateProperty.succeed(columnName, property => property.copy(people = f(property.people)))

    def add(people: Seq[Id]): UpdateProperty[PatchedPeople] = update(_ ++ people)
    def add(people: Id): UpdateProperty[PatchedPeople]      = add(List(people))
  }

  final case class PatchedColumnFiles(columnName: String) {
    def set(files: Seq[Link]): SetProperty[PatchedFiles] = SetProperty(columnName, PatchedFiles(files))

    def update(f: Seq[Link] => Seq[Link]): UpdateProperty[PatchedFiles] =
      UpdateProperty.succeed(columnName, property => property.copy(files = f(property.files)))

    def add(files: Seq[Link]): UpdateProperty[PatchedFiles]              = update(_ ++ files)
    def add(file: Link): UpdateProperty[PatchedFiles]                    = add(List(file))
    def filter(predicate: Link => Boolean): UpdateProperty[PatchedFiles] = update(_.filter(predicate))
  }

  final case class PatchedColumnUrl(columnName: String) {
    def set(url: String): SetProperty[PatchedUrl] = SetProperty(columnName, PatchedUrl(url))
  }

  final case class PatchedColumnEmail(columnName: String) {
    def set(email: String): SetProperty[PatchedEmail] = SetProperty(columnName, PatchedEmail(email))

    def update(f: String => String): UpdateProperty[PatchedEmail] =
      UpdateProperty.succeed(columnName, property => property.copy(email = f(property.email)))
  }

  final case class PatchedColumnPhoneNumber(columnName: String) {
    def set(phoneNumber: String): SetProperty[PatchedPhoneNumber] = SetProperty(columnName, PatchedPhoneNumber(phoneNumber))

    def update(f: String => String): UpdateProperty[PatchedPhoneNumber] =
      UpdateProperty.succeed(columnName, property => property.copy(phoneNumber = f(property.phoneNumber)))
  }
}
