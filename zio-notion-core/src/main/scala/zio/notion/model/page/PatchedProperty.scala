package zio.notion.model.page

import io.circe.{Encoder, Json}

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.magnolia.{NoDiscriminantNoNullEncoderDerivation, PatchedPropertyEncoderDerivation}
import zio.notion.model.page.property.Link

import java.time.LocalDate

sealed trait PatchedProperty

// TODO: Add formula and rollup patches
object PatchedProperty {
  final case class PatchedTitle(title: Seq[RichTextData]) extends PatchedProperty

  object PatchedTitle {
    implicit val encoder: Encoder[PatchedTitle] = PatchedPropertyEncoderDerivation.gen[PatchedTitle]
  }

  final case class PatchedRichText(richText: Seq[RichTextData]) extends PatchedProperty

  object PatchedRichText {
    implicit val encoder: Encoder[PatchedRichText] = PatchedPropertyEncoderDerivation.gen[PatchedRichText]
  }

  final case class PatchedNumber(number: Double) extends PatchedProperty

  object PatchedNumber {
    implicit val encoder: Encoder[PatchedNumber] = PatchedPropertyEncoderDerivation.gen[PatchedNumber]
  }

  final case class PatchedCheckbox(checkbox: Boolean) extends PatchedProperty

  object PatchedCheckbox {
    implicit val encoder: Encoder[PatchedCheckbox] = PatchedPropertyEncoderDerivation.gen[PatchedCheckbox]
  }

  final case class PatchedSelect(id: Option[String], name: Option[String]) extends PatchedProperty

  object PatchedSelect {
    implicit val encoder: Encoder[PatchedSelect] = PatchedPropertyEncoderDerivation.gen[PatchedSelect]
  }

  final case class PatchedMultiSelect(multiSelect: List[PatchedSelect]) extends PatchedProperty

  object PatchedMultiSelect {

    implicit val encoder: Encoder[PatchedMultiSelect] =
      (property: PatchedMultiSelect) => {
        val encodedMultiSelect = property.multiSelect.map(NoDiscriminantNoNullEncoderDerivation.gen[PatchedSelect].apply)
        Json.obj("multi_select" -> Json.arr(encodedMultiSelect: _*))
      }
  }

  final case class PatchedDate(start: LocalDate, end: Option[LocalDate], timeZone: Option[String]) extends PatchedProperty

  object PatchedDate {
    implicit val encoder: Encoder[PatchedDate] = PatchedPropertyEncoderDerivation.gen[PatchedDate]
  }

  final case class PatchedPeople(people: Seq[Id]) extends PatchedProperty

  object PatchedPeople {
    implicit val encoder: Encoder[PatchedPeople] = PatchedPropertyEncoderDerivation.gen[PatchedPeople]
  }

  final case class PatchedRelation(relation: Seq[Id]) extends PatchedProperty

  object PatchedRelation {
    implicit val encoder: Encoder[PatchedRelation] = PatchedPropertyEncoderDerivation.gen[PatchedRelation]
  }

  final case class PatchedFiles(files: Seq[Link]) extends PatchedProperty

  object PatchedFiles {
    implicit val encoder: Encoder[PatchedFiles] = PatchedPropertyEncoderDerivation.gen[PatchedFiles]
  }

  final case class PatchedUrl(url: String) extends PatchedProperty

  object PatchedUrl {
    implicit val encoder: Encoder[PatchedUrl] = PatchedPropertyEncoderDerivation.gen[PatchedUrl]
  }

  final case class PatchedEmail(email: String) extends PatchedProperty

  object PatchedEmail {
    implicit val encoder: Encoder[PatchedEmail] = PatchedPropertyEncoderDerivation.gen[PatchedEmail]
  }

  final case class PatchedPhoneNumber(phoneNumber: String) extends PatchedProperty

  object PatchedPhoneNumber {
    implicit val encoder: Encoder[PatchedPhoneNumber] = PatchedPropertyEncoderDerivation.gen[PatchedPhoneNumber]
  }

  implicit val encoder: Encoder[PatchedProperty] = PatchedPropertyEncoderDerivation.gen[PatchedProperty]
}
