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
    def set(title: Seq[RichTextData.Text]): SetProperty = SetProperty(columnName, PatchedTitle(title))
    def set(title: String): SetProperty                 = set(Seq(RichTextData.default(title, Annotations.default)))

    def update(f: Seq[RichTextData] => Seq[RichTextData]): UpdateProperty =
      UpdateProperty.succeed[PatchedTitle](columnName, property => property.copy(title = f(property.title)))

    def capitalize: UpdateProperty =
      update(_.map {
        case d: RichTextData.Text => d.copy(text = d.text.copy(content = d.text.content.capitalize), plainText = d.plainText.capitalize)
        case d                    => d
      })
  }

  final case class PatchedColumnRichText(columnName: String) {
    def set(title: Seq[RichTextData.Text]): SetProperty = SetProperty(columnName, PatchedRichText(title))
    def set(title: String): SetProperty                 = set(Seq(RichTextData.default(title, Annotations.default)))

    def update(f: Seq[RichTextData] => Seq[RichTextData]): UpdateProperty =
      UpdateProperty.succeed[PatchedRichText](columnName, property => property.copy(richText = f(property.richText)))

    def write(text: String, annotations: Annotations = Annotations.default): SetProperty =
      set(List(RichTextData.default(text, annotations)))

    def annotate(f: Annotations => Annotations): UpdateProperty =
      update(_.map {
        case d: RichTextData.Text     => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Mention  => d.copy(annotations = f(d.annotations))
        case d: RichTextData.Equation => d.copy(annotations = f(d.annotations))
      })

    def reset: UpdateProperty               = annotate(_ => Annotations.default)
    def bold: UpdateProperty                = annotate(_.copy(bold = true))
    def italic: UpdateProperty              = annotate(_.copy(italic = true))
    def strikethrough: UpdateProperty       = annotate(_.copy(strikethrough = true))
    def underline: UpdateProperty           = annotate(_.copy(underline = true))
    def code: UpdateProperty                = annotate(_.copy(code = true))
    def color(color: Color): UpdateProperty = annotate(_.copy(color = color))
  }

  final case class PatchedColumnNumber(columnName: String) {
    def set(number: Double): SetProperty            = SetProperty(columnName, PatchedNumber(number))
    def update(f: Double => Double): UpdateProperty = maybeUpdate(p => Right(f(p)))

    def maybeUpdate(f: Double => Either[NotionError, Double]): UpdateProperty =
      UpdateProperty(
        columnName,
        UpdateProperty.Transform.GenericWithType[PatchedNumber] {
          case Some(property) => f(property.number).map(number => Some(property.copy(number = number)))
          case None           => ???
        }
      )
    // UpdateProperty(columnName, property => f(property.number).map(number => property.copy(number = number)))

    def divide(number: Double): UpdateProperty = update(_ / number) // TODO: Transform to maybe update
    def add(number: Double): UpdateProperty    = update(_ + number)
    def minus(number: Double): UpdateProperty  = update(_ - number)
    def times(number: Double): UpdateProperty  = update(_ * number)
    def pow(number: Double): UpdateProperty    = update(Math.pow(_, number))
    def floor: UpdateProperty                  = update(Math.floor)
    def ceil: UpdateProperty                   = update(Math.ceil)
  }

  final case class PatchedColumnCheckbox(columnName: String) {
    def set(checkbox: Boolean): SetProperty = SetProperty(columnName, PatchedCheckbox(checkbox))

    def update(f: Boolean => Boolean): UpdateProperty =
      UpdateProperty.succeed[PatchedCheckbox](columnName, property => property.copy(checkbox = f(property.checkbox)))

    def check: SetProperty      = set(true)
    def uncheck: SetProperty    = set(false)
    def reverse: UpdateProperty = update(!_)
  }

  final case class PatchedColumnSelect(columnName: String) {
    def set(id: Option[String], name: Option[String]): SetProperty = SetProperty(columnName, PatchedSelect(id, name))

    def setUsingId(id: String): SetProperty     = set(Some(id), None)
    def setUsingName(name: String): SetProperty = set(None, Some(name))
  }

  final case class PatchedColumnMultiSelect(columnName: String) {
    def set(selects: List[PatchedSelect]): SetProperty = SetProperty(columnName, PatchedMultiSelect(selects))

    def update(f: List[PatchedSelect] => List[PatchedSelect]): UpdateProperty =
      UpdateProperty.succeed[PatchedMultiSelect](columnName, property => property.copy(multiSelect = f(property.multiSelect)))

    def removeUsingIdIfExists(id: String): UpdateProperty     = update(_.filterNot(_.id.contains(id)))
    def removeUsingNameIfExists(name: String): UpdateProperty = update(_.filterNot(_.name.contains(name)))
    def addUsingId(id: String): UpdateProperty                = update(_ :+ PatchedSelect(Some(id), None))
    def addUsingName(name: String): UpdateProperty            = update(_ :+ PatchedSelect(None, Some(name)))
  }

  final case class PatchedColumnDate(columnName: String) {
    private val utc = ZoneOffset.UTC

    def set(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String]): SetProperty =
      SetProperty(columnName, PatchedDate(start, end, timeZone))

    def startAt(date: OffsetDateTime): SetProperty = set(date, None, None)
    def startAt(date: LocalDateTime): SetProperty  = startAt(date.atOffset(utc))
    def startAt(date: LocalDate): SetProperty      = startAt(date.atStartOfDay())

    def endAt(f: OffsetDateTime => OffsetDateTime): UpdateProperty =
      UpdateProperty.succeed[PatchedDate](columnName, property => property.copy(end = Some(f(property.start))))
    def endAt(date: OffsetDateTime): UpdateProperty = endAt(_ => date)
    def endAt(date: LocalDateTime): UpdateProperty  = endAt(date.atOffset(utc))
    def endAt(date: LocalDate): UpdateProperty      = endAt(date.atStartOfDay())

    def between(start: OffsetDateTime, end: OffsetDateTime): SetProperty = set(start, Some(end), None)
    def between(start: LocalDateTime, end: LocalDateTime): SetProperty   = between(start.atOffset(utc), end.atOffset(utc))
    def between(start: LocalDate, end: LocalDate): SetProperty           = between(start.atStartOfDay(), end.atStartOfDay())

    def today: zio.UIO[SetProperty] = Clock.currentDateTime.map(startAt)
  }

  final case class PatchedColumnPeople(columnName: String) {
    def set(people: Seq[Id]): SetProperty = SetProperty(columnName, PatchedPeople(people))

    def update(f: Seq[Id] => Seq[Id]): UpdateProperty =
      UpdateProperty.succeed[PatchedPeople](columnName, property => property.copy(people = f(property.people)))

    def add(people: Seq[Id]): UpdateProperty = update(_ ++ people)
    def add(people: Id): UpdateProperty      = add(List(people))
  }

  final case class PatchedColumnFiles(columnName: String) {
    def set(files: Seq[Link]): SetProperty = SetProperty(columnName, PatchedFiles(files))

    def update(f: Seq[Link] => Seq[Link]): UpdateProperty =
      UpdateProperty.succeed[PatchedFiles](columnName, property => property.copy(files = f(property.files)))

    def add(files: Seq[Link]): UpdateProperty              = update(_ ++ files)
    def add(file: Link): UpdateProperty                    = add(List(file))
    def filter(predicate: Link => Boolean): UpdateProperty = update(_.filter(predicate))
  }

  final case class PatchedColumnUrl(columnName: String) {
    def set(url: String): SetProperty = SetProperty(columnName, PatchedUrl(url))
  }

  final case class PatchedColumnEmail(columnName: String) {
    def set(email: String): SetProperty = SetProperty(columnName, PatchedEmail(email))

    def update(f: String => String): UpdateProperty =
      UpdateProperty.succeed[PatchedEmail](columnName, property => property.copy(email = f(property.email)))
  }

  final case class PatchedColumnPhoneNumber(columnName: String) {
    def set(phoneNumber: String): SetProperty = SetProperty(columnName, PatchedPhoneNumber(phoneNumber))

    def update(f: String => String): UpdateProperty =
      UpdateProperty.succeed[PatchedPhoneNumber](columnName, property => property.copy(phoneNumber = f(property.phoneNumber)))
  }
}
