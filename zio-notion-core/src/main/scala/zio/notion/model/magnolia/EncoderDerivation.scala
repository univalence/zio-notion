package zio.notion.model.magnolia

import io.circe.Encoder
import magnolia1.{Magnolia, SealedTrait}

class EncoderDerivation {
  type Typeclass[T] = Encoder[T]

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.split(value) { sub =>
        sub.typeclass.apply(sub.cast(value))
      }

  implicit def gen[T]: Encoder[T] = macro Magnolia.gen[T]
}
