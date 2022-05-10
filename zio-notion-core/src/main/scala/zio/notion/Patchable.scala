package zio.notion

import zio.notion.model.page.patch.PatchedProperty
import zio.notion.model.page.property.Property

trait Patchable[I <: Property, O <: PatchedProperty] {
  def patch(input: I): Option[O]
}
