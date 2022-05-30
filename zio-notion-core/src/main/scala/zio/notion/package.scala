package zio

import zio.notion.model.page.PatchedProperty._
import zio.notion.model.page.Property._

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

  implicit val patchableTitle: Patchable[Title, PatchedTitle] = (input: Title) => Some(PatchedTitle(input.title))

  implicit val patchableRichText: Patchable[RichText, PatchedRichText] = (input: RichText) => Some(PatchedRichText(input.richText))

  implicit val patchablePeople: Patchable[People, PatchedPeople] = (input: People) => Some(PatchedPeople(input.people))

  implicit val patchableRelation: Patchable[Relation, PatchedRelation] = (input: Relation) => Some(PatchedRelation(input.relation))
}
