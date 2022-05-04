package zio.notion.model

import io.circe.generic.extras._

import zio.{Clock, ZIO}
import zio.notion.PropertyUpdater.Transformation

import java.time.LocalDate

@ConfiguredJsonCodec sealed trait Property

// https://github.com/makenotion/notion-sdk-js/blob/main/src/api-endpoints.ts#L8870
object Property {
  final case class Number(id: String, number: Option[Double]) extends Property

  object Number {
    def update(f: Number => Number): Transformation[Any, Nothing, Number] = number => ZIO.succeed(f(number))
  }

  final case class Url(id: String, url: Option[String]) extends Property

  object Url {
    def update(f: Url => Url): Transformation[Any, Nothing, Url] = url => ZIO.succeed(f(url))
  }

  final case class Select(id: String, select: Option[SelectData]) extends Property

  object Select {
    def update(f: Select => Select): Transformation[Any, Nothing, Select] = select => ZIO.succeed(f(select))
  }

  final case class MultiSelect(id: String, multiSelect: List[SelectData]) extends Property

  object MultiSelect {
    def update(f: MultiSelect => MultiSelect): Transformation[Any, Nothing, MultiSelect] = multiSelect => ZIO.succeed(f(multiSelect))
  }

  final case class Date(id: String, date: Option[DateData]) extends Property { self =>
    def withStartDate(startDate: LocalDate): Date = {
      val dateData =
        self.date match {
          case None       => DateData(startDate, None, None)
          case Some(data) => data.copy(start = startDate)
        }

      copy(date = Some(dateData))
    }
  }

  object Date {
    def update(f: Date => Date): Transformation[Any, Nothing, Date] = title => ZIO.succeed(f(title))

    def startAt(newDate: LocalDate): Transformation[Any, Nothing, Date] = update(_.withStartDate(newDate))

    def endAt(newDate: LocalDate): Transformation[Any, Nothing, Date] = update(date => date.copy(date = date.date.map(_.copy(end = Some(newDate)))))

    def between(from: LocalDate, to: LocalDate): Transformation[Any, Nothing, Date] = startAt(from) andThen endAt(to)

    def now: Transformation[Any, Nothing, Date] = date => Clock.localDateTime.map(_.toLocalDate).flatMap(d => startAt(d).transform(date))
  }

  final case class Email(id: String, email: Option[String]) extends Property

  object Email {
    def update(f: Email => Email): Transformation[Any, Nothing, Email] = email => ZIO.succeed(f(email))
  }

  final case class PhoneNumber(id: String, phoneNumber: Option[String]) extends Property

  object PhoneNumber {
    def update(f: PhoneNumber => PhoneNumber): Transformation[Any, Nothing, PhoneNumber] = phoneNumber => ZIO.succeed(f(phoneNumber))
  }

  final case class Checkbox(id: String, checkbox: Option[Boolean]) extends Property

  object Checkbox {
    def update(f: Checkbox => Checkbox): Transformation[Any, Nothing, Checkbox] = checkbox => ZIO.succeed(f(checkbox))

    def check: Transformation[Any, Nothing, Checkbox] = update(_.copy(checkbox = Some(true)))

    def uncheck: Transformation[Any, Nothing, Checkbox] = update(_.copy(checkbox = Some(false)))

    def reverse: Transformation[Any, Nothing, Checkbox] = update(checkbox => checkbox.copy(checkbox = checkbox.checkbox.map(!_)))
  }

  final case class Files(id: String, files: Seq[Link]) extends Property

  object Files {
    def update(f: Files => Files): Transformation[Any, Nothing, Files] = files => ZIO.succeed(f(files))
  }

  final case class CreatedBy(id: String, createdBy: UserId) extends Property

  final case class CreatedTime(id: String, createdTime: String) extends Property

  final case class LastEditedBy(id: String, lastEditedBy: UserId) extends Property

  final case class LastEditedTime(id: String, lastEditedTime: String) extends Property

  final case class Formula(id: String, formula: FormulaData) extends Property

  object Formula {
    def update(f: Formula => Formula): Transformation[Any, Nothing, Formula] = formula => ZIO.succeed(f(formula))
  }

  final case class Title(id: String, title: Seq[RichTextData]) extends Property

  object Title {
    def defaultData(title: String): Seq[RichTextData] = Seq(RichTextData.Text(RichTextData.Text.TextData(title, None), Annotations.default, title, None))

    def update(f: Title => Title): Transformation[Any, Nothing, Title] = title => ZIO.succeed(f(title))

    def updateAsText[R, E](f: String => ZIO[R, E, String]): Transformation[R, E, Title] =
      title => {
        val maybeOldTitle = title.title.headOption.map(_.asInstanceOf[RichTextData.Text].plainText)
        maybeOldTitle match {
          case None          => ZIO.succeed(title) // TODO: An error if there is no text to update
          case Some(oldText) => f(oldText).map(newTitle => title.copy(title = Title.defaultData(newTitle)))
        }
      }

    def rename(newTitle: String): Transformation[Any, Nothing, Title] = update(_.copy(title = Title.defaultData(newTitle)))
  }

  final case class RichText(id: String, richText: Seq[RichTextData]) extends Property

  object RichText {
    def update[R, E](f: RichText => ZIO[R, E, RichText]): Transformation[R, E, RichText] = text => f(text)
  }

  final case class People(id: String, people: Seq[UserId]) extends Property

  object People {
    def update(f: People => People): Transformation[Any, Nothing, People] = people => ZIO.succeed(f(people))

    def clean: Transformation[Any, Nothing, People] = update(_.copy(people = Seq.empty))
  }

  final case class Rollup(id: String, rollup: RollupData) extends Property

  object Rollup {
    def update(f: Rollup => Rollup): Transformation[Any, Nothing, Rollup] = rollup => ZIO.succeed(f(rollup))
  }
}
