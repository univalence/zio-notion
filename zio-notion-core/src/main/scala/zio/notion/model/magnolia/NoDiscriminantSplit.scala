package zio.notion.model.magnolia

import io.circe.Encoder
import magnolia1.SealedTrait

trait NoDiscriminantSplit {

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (value: T) => ctx.split(value)(subtype => subtype.typeclass.apply(subtype.cast(value)))
}
