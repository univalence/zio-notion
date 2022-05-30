package zio.notion.model.page

import io.circe.generic.extras._

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.property.Link
import zio.notion.model.page.property.data.{DateData, FormulaData, RollupData, SelectData}

@ConfiguredJsonCodec sealed trait Property {
  def id: String
}

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

  final case class CreatedBy(id: String, createdBy: Id) extends Property

  final case class CreatedTime(id: String, createdTime: String) extends Property

  final case class LastEditedBy(id: String, lastEditedBy: Id) extends Property

  final case class LastEditedTime(id: String, lastEditedTime: String) extends Property

  final case class Formula(id: String, formula: FormulaData) extends Property

  final case class Title(id: String, title: Seq[RichTextData]) extends Property

  final case class RichText(id: String, richText: Seq[RichTextData]) extends Property

  final case class People(id: String, people: Seq[Id]) extends Property

  final case class Relation(id: String, relation: Seq[Id]) extends Property

  final case class Rollup(id: String, rollup: RollupData) extends Property
}
