package zio.notion.model.page

import io.circe.{Encoder, Json}

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.magnolia.{NoDiscriminantNoNullEncoderDerivation, PatchedPropertyEncoderDerivation}
import zio.notion.model.page.property.Link
import zio.notion.model.user.User

import java.time.{LocalDate, OffsetDateTime}

sealed trait PatchedProperty

// TODO: Add formula and rollup patches
object PatchedProperty {
  final case class PatchedTitle(title: Seq[RichTextFragment])                                                    extends PatchedProperty
  final case class PatchedRichText(richText: Seq[RichTextFragment])                                              extends PatchedProperty
  final case class PatchedNumber(number: Double)                                                                 extends PatchedProperty
  final case class PatchedCheckbox(checkbox: Boolean)                                                            extends PatchedProperty
  final case class PatchedSelect(id: Option[String], name: Option[String])                                       extends PatchedProperty
  final case class PatchedMultiSelect(multiSelect: List[PatchedSelect])                                          extends PatchedProperty
  final case class PatchedDate(start: LocalDate, end: Option[LocalDate])                                         extends PatchedProperty
  final case class PatchedDateTime(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String]) extends PatchedProperty
  final case class PatchedPeople(people: Seq[User])                                                              extends PatchedProperty
  final case class PatchedRelation(relation: Seq[Id])                                                            extends PatchedProperty
  final case class PatchedFiles(files: Seq[Link])                                                                extends PatchedProperty
  final case class PatchedUrl(url: String)                                                                       extends PatchedProperty
  final case class PatchedEmail(email: String)                                                                   extends PatchedProperty
  final case class PatchedPhoneNumber(phoneNumber: String)                                                       extends PatchedProperty
  final case class PatchedStatus(id: Option[String], name: Option[String])                                       extends PatchedProperty

  object PatchedTitle {
    implicit val encoder: Encoder[PatchedTitle] = PatchedPropertyEncoderDerivation.gen[PatchedTitle]
  }

  object PatchedRichText {
    implicit val encoder: Encoder[PatchedRichText] = PatchedPropertyEncoderDerivation.gen[PatchedRichText]
  }

  object PatchedNumber {
    implicit val encoder: Encoder[PatchedNumber] = PatchedPropertyEncoderDerivation.gen[PatchedNumber]
  }

  object PatchedCheckbox {
    implicit val encoder: Encoder[PatchedCheckbox] = PatchedPropertyEncoderDerivation.gen[PatchedCheckbox]
  }

  object PatchedSelect {
    implicit val encoder: Encoder[PatchedSelect] = PatchedPropertyEncoderDerivation.gen[PatchedSelect]
  }

  object PatchedStatus {
    implicit val encoder: Encoder[PatchedStatus] = PatchedPropertyEncoderDerivation.gen[PatchedStatus]
  }

  object PatchedMultiSelect {

    implicit val encoder: Encoder[PatchedMultiSelect] =
      (property: PatchedMultiSelect) => {
        val encodedMultiSelect = property.multiSelect.map(NoDiscriminantNoNullEncoderDerivation.gen[PatchedSelect].apply)
        Json.obj("multi_select" -> Json.arr(encodedMultiSelect: _*))
      }
  }

  object PatchedDate {
    implicit val encoder: Encoder[PatchedDate] = PatchedPropertyEncoderDerivation.gen[PatchedDate]
  }

  object PatchedDateTime {
    implicit val encoder: Encoder[PatchedDateTime] = PatchedPropertyEncoderDerivation.gen[PatchedDateTime]
  }

  object PatchedPeople {
    implicit val encoder: Encoder[PatchedPeople] = PatchedPropertyEncoderDerivation.gen[PatchedPeople]
  }

  object PatchedRelation {
    implicit val encoder: Encoder[PatchedRelation] = PatchedPropertyEncoderDerivation.gen[PatchedRelation]
  }

  object PatchedFiles {
    implicit val encoder: Encoder[PatchedFiles] = PatchedPropertyEncoderDerivation.gen[PatchedFiles]
  }

  object PatchedUrl {
    implicit val encoder: Encoder[PatchedUrl] = PatchedPropertyEncoderDerivation.gen[PatchedUrl]
  }

  object PatchedEmail {
    implicit val encoder: Encoder[PatchedEmail] = PatchedPropertyEncoderDerivation.gen[PatchedEmail]
  }

  object PatchedPhoneNumber {
    implicit val encoder: Encoder[PatchedPhoneNumber] = PatchedPropertyEncoderDerivation.gen[PatchedPhoneNumber]
  }

  implicit val encoder: Encoder[PatchedProperty] = PatchedPropertyEncoderDerivation.gen[PatchedProperty]
}
