package zio

import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.patch.PatchedProperty._
import zio.notion.model.page.property.Property._

package object notion {
  implicit val patchableNumber: Patchable[Number, PatchedNumber] = (input: Number) => input.number.map(PatchedNumber.apply)

  // We can't update a select from an existing one.
  implicit val patchableSelect: Patchable[Select, PatchedSelect] = (_: Select) => None

  implicit val patchableMultiSelect: Patchable[MultiSelect, PatchedMultiSelect] =
    (input: MultiSelect) => Some(PatchedMultiSelect(input.multiSelect.map(data => PatchedSelect(Some(data.id), Some(data.name)))))

  implicit val patchableUrl: Patchable[Url, PatchedUrl] = (input: Url) => input.url.map(PatchedUrl.apply)

  implicit val patchableDate: Patchable[Date, PatchedDate] =
    (input: Date) => input.date.map(date => PatchedDate(date.start, date.end, date.timeZone))

  implicit val patchableEmail: Patchable[Email, PatchedEmail] = (input: Email) => input.email.map(PatchedEmail.apply)

  implicit val patchablePhoneNumber: Patchable[PhoneNumber, PatchedPhoneNumber] =
    (input: PhoneNumber) => input.phoneNumber.map(PatchedPhoneNumber.apply)

  implicit val patchableCheckbox: Patchable[Checkbox, PatchedCheckbox] = (input: Checkbox) => input.checkbox.map(PatchedCheckbox.apply)

  implicit val patchableFiles: Patchable[Files, PatchedFiles] = (input: Files) => Some(PatchedFiles(input.files))

  implicit val patchableTitle: Patchable[Title, PatchedTitle] =
    (input: Title) =>
      Some(
        PatchedTitle(
          input.title
            .map {
              case RichTextData.Text(_, _, plainText, _)     => plainText
              case RichTextData.Mention(_, _, plainText, _)  => plainText
              case RichTextData.Equation(_, _, plainText, _) => plainText
            }
            .reduce(_ + " " + _)
        )
      )

}