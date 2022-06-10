package zio.notion.model.page

import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.generic.extras._

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.PatchedProperty._
import zio.notion.model.page.property.Link
import zio.notion.model.page.property.data.{FormulaData, RollupData, SelectData}
import zio.notion.model.user.User

import java.time.{LocalDate, OffsetDateTime}

object ToPatchedProperty {

  def apply(property: Property): Option[PatchedProperty] =
    property match {

      case Property.Number(_, Some(number))           => Some(PatchedNumber(number))
      case Property.Url(_, Some(url))                 => Some(PatchedUrl(url))
      case Property.Date(_, Some(date))               => Some(PatchedDate(date.start, date.end))
      case Property.DateTime(_, Some(date))           => Some(PatchedDateTime(date.start, date.end, date.timeZone))
      case Property.Email(_, Some(email))             => Some(PatchedEmail(email))
      case Property.PhoneNumber(_, Some(phoneNumber)) => Some(PatchedPhoneNumber(phoneNumber))
      case Property.Checkbox(_, Some(checkbox))       => Some(PatchedCheckbox(checkbox))
      case Property.Files(_, files)                   => Some(PatchedFiles(files))
      case Property.Title(_, title)                   => Some(PatchedTitle(title))
      case Property.RichText(_, richText)             => Some(PatchedRichText(richText))
      case Property.People(_, people)                 => Some(PatchedPeople(people))
      case Property.Relation(_, relation)             => Some(PatchedRelation(relation))
      case Property.MultiSelect(_, multiSelect) =>
        val selects = multiSelect.map(data => PatchedSelect(Some(data.id), Some(data.name)))
        Some(PatchedMultiSelect(selects))
      // We can't update a select from an existing one.
      case _ => None
    }
}

sealed trait Property { def id: String }

object Property {

  @ConfiguredJsonCodec(decodeOnly = true) final case class Number(id: String, number: Option[Double])             extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Url(id: String, url: Option[String])                   extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Select(id: String, select: Option[SelectData])         extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class MultiSelect(id: String, multiSelect: List[SelectData]) extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Date(id: String, date: Option[Date.Data])              extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class DateTime(id: String, date: Option[DateTime.Data])      extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Email(id: String, email: Option[String])               extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class PhoneNumber(id: String, phoneNumber: Option[String])   extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Checkbox(id: String, checkbox: Option[Boolean])        extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Files(id: String, files: Seq[Link])                    extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Title(id: String, title: Seq[RichTextData])            extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class RichText(id: String, richText: Seq[RichTextData])      extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class People(id: String, people: Seq[User])                  extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Relation(id: String, relation: Seq[Id])                extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class CreatedBy(id: String, createdBy: Id)                   extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class CreatedTime(id: String, createdTime: String)           extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class LastEditedBy(id: String, lastEditedBy: Id)             extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class LastEditedTime(id: String, lastEditedTime: String)     extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Formula(id: String, formula: FormulaData)              extends Property
  @ConfiguredJsonCodec(decodeOnly = true) final case class Rollup(id: String, rollup: RollupData)                 extends Property

  object Date {
    @ConfiguredJsonCodec final case class Data(start: LocalDate, end: Option[LocalDate])
  }

  object DateTime {
    @ConfiguredJsonCodec final case class Data(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String])
  }

  implicit val propertyDecoder: Decoder[Property] =
    (c: HCursor) =>
      c.downField("type").as[String] match {
        case Right(value) =>
          value match {
            case "number"           => Decoder[Number].apply(c)
            case "url"              => Decoder[Url].apply(c)
            case "select"           => Decoder[Select].apply(c)
            case "multi_select"     => Decoder[MultiSelect].apply(c)
            case "date"             => Decoder[Date].apply(c) orElse Decoder[DateTime].apply(c)
            case "email"            => Decoder[Email].apply(c)
            case "phone_number"     => Decoder[PhoneNumber].apply(c)
            case "checkbox"         => Decoder[Checkbox].apply(c)
            case "files"            => Decoder[Files].apply(c)
            case "title"            => Decoder[Title].apply(c)
            case "rich_text"        => Decoder[RichText].apply(c)
            case "people"           => Decoder[People].apply(c)
            case "relation"         => Decoder[Relation].apply(c)
            case "created_by"       => Decoder[CreatedBy].apply(c)
            case "created_time"     => Decoder[CreatedTime].apply(c)
            case "last_edited_by"   => Decoder[LastEditedBy].apply(c)
            case "last_edited_time" => Decoder[LastEditedTime].apply(c)
            case "formula"          => Decoder[Formula].apply(c)
            case "rollup"           => Decoder[Rollup].apply(c)
            case v                  => Left(DecodingFailure(s"The type $v is unknown", c.history))
          }
        case _ => Left(DecodingFailure(s"Missing required field 'type'", c.history))
      }
}
