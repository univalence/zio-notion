package zio.notion

import zio.notion.model.page.{PatchedProperty, Property}

trait Patchable[I <: Property, O <: PatchedProperty] {
  def patch(input: I): Option[O]
}
