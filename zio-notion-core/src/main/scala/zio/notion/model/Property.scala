package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait Property

// https://github.com/makenotion/notion-sdk-js/blob/main/src/api-endpoints.ts#L8870
object Property {
  final case class Number(id: String, number: Option[Double]) extends Property

  final case class Url(id: String, url: Option[String]) extends Property

  final case class Select(id: String, select: Option[SelectData]) extends Property

  final case class MultiSelect(id: String, multiSelect: List[SelectData]) extends Property

  final case class Date(id: String, date: Option[DateData]) extends Property

  final case class Email(id: String, email: Option[String]) extends Property

  final case class PhoneNumber(id: String, phoneNumber: Option[String]) extends Property

  final case class Checkbox(id: String, checkbox: Option[Boolean]) extends Property

  final case class Files(id: String, files: Seq[Link]) extends Property

  final case class CreatedBy(id: String, createdBy: UserId) extends Property

  final case class CreatedTime(id: String, createdTime: String) extends Property

  final case class LastEditedBy(id: String, lastEditedBy: UserId) extends Property

  final case class LastEditedTime(id: String, lastEditedTime: String) extends Property

  final case class Formula(id: String, formula: FormulaData) extends Property

  final case class Title(id: String, title: Seq[RichTextData]) extends Property

  final case class RichText(id: String, richText: Seq[RichTextData]) extends Property

  final case class People(id: String, people: Seq[UserId]) extends Property

  final case class Rollup(id: String, rollup: RollupData) extends Property

  object Title {
    def update(f: Title => Title): Title => Title = f

    def rename(newTitle: String): Title => Title =
      update(title => title.copy(title = Seq(RichTextData.Text(RichTextData.Text.TextData(newTitle, None), Annotations.default, newTitle, None))))
  }
}
