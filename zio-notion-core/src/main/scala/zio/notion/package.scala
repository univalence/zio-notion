package zio

import zio.notion.model.page.patch.PatchedProperty._
import zio.notion.model.page.property.Property._

package object notion {
  implicit val patchableNumber: Patchable[Number, PatchedNumber] = (input: Number) => input.number.map(PatchedNumber.apply)

  // We can't update a select from an existing one.
  implicit val patchableSelect: Patchable[Select, PatchedSelect] = (_: Select) => None

  implicit val patchableUrl: Patchable[Url, PatchedUrl] = (input: Url) => input.url.map(PatchedUrl.apply)
}
