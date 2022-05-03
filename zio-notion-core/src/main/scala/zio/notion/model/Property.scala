package zio.notion.model

import io.circe.generic.extras._

import zio.{Clock, UIO}

import java.time.LocalDate

@ConfiguredJsonCodec sealed trait Property

// https://github.com/makenotion/notion-sdk-js/blob/main/src/api-endpoints.ts#L8870
object Property {
  final case class Number(id: String, number: Option[Double]) extends Property

  object Number {
    def update(f: Number => Number): Number => Number = f
  }

  final case class Url(id: String, url: Option[String]) extends Property

  object Url {
    def update(f: Url => Url): Url => Url = f
  }

  final case class Select(id: String, select: Option[SelectData]) extends Property

  object Select {
    def update(f: Select => Select): Select => Select = f
  }

  final case class MultiSelect(id: String, multiSelect: List[SelectData]) extends Property

  object MultiSelect {
    def update(f: MultiSelect => MultiSelect): MultiSelect => MultiSelect = f
  }

  final case class Date(id: String, date: Option[DateData]) extends Property

  object Date {
    def update(f: Date => Date): Date => Date = f

    def now: Date => UIO[Date] =
      date =>
        Clock.localDateTime.map(dateTime =>
          Date(id = date.id, date = Some(DateData(start = dateTime.toLocalDate, date.date.flatMap(_.end), date.date.flatMap(_.timeZone))))
        )

    def startAt(newDate: LocalDate): Date => Date = date => date.copy(date = date.date.map(_.copy(start = newDate)))

    def endAt(newDate: LocalDate): Date => Date = date => date.copy(date = date.date.map(_.copy(end = Some(newDate))))

    def between(from: LocalDate, to: LocalDate): Date => Date = startAt(from) andThen endAt(to)
  }

  final case class Email(id: String, email: Option[String]) extends Property

  object Email {
    def update(f: Email => Email): Email => Email = f
  }

  final case class PhoneNumber(id: String, phoneNumber: Option[String]) extends Property

  object PhoneNumber {
    def update(f: PhoneNumber => PhoneNumber): PhoneNumber => PhoneNumber = f
  }

  final case class Checkbox(id: String, checkbox: Option[Boolean]) extends Property

  object Checkbox {
    def update(f: Checkbox => Checkbox): Checkbox => Checkbox = f

    def check: Checkbox => Checkbox = _.copy(checkbox = Some(true))

    def uncheck: Checkbox => Checkbox = _.copy(checkbox = Some(false))

    def reverse: Checkbox => Checkbox = checkbox => checkbox.copy(checkbox = checkbox.checkbox.map(!_))
  }

  final case class Files(id: String, files: Seq[Link]) extends Property

  object Files {
    def update(f: Files => Files): Files => Files = f
  }

  final case class CreatedBy(id: String, createdBy: UserId) extends Property

  final case class CreatedTime(id: String, createdTime: String) extends Property

  final case class LastEditedBy(id: String, lastEditedBy: UserId) extends Property

  final case class LastEditedTime(id: String, lastEditedTime: String) extends Property

  final case class Formula(id: String, formula: FormulaData) extends Property

  object Formula {
    def update(f: Formula => Formula): Formula => Formula = f
  }

  final case class Title(id: String, title: Seq[RichTextData]) extends Property

  object Title {
    def update(f: Title => Title): Title => Title = f

    def rename(newTitle: String): Title => Title =
      _.copy(title = Seq(RichTextData.Text(RichTextData.Text.TextData(newTitle, None), Annotations.default, newTitle, None)))
  }

  final case class RichText(id: String, richText: Seq[RichTextData]) extends Property

  object RichText {
    def update(f: RichText => RichText): RichText => RichText = f
  }

  final case class People(id: String, people: Seq[UserId]) extends Property

  object People {
    def update(f: People => People): People => People = f

    def clean: People => People = _.copy(people = Seq.empty)
  }

  final case class Rollup(id: String, rollup: RollupData) extends Property

  object Rollup {
    def update(f: Rollup => Rollup): Rollup => Rollup = f
  }
}
