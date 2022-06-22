package zio.notion.model.magnolia

import io.circe.Encoder
import magnolia1.Magnolia

class BaseEncoderDerivation {
  type Typeclass[T] = Encoder[T]

  implicit def gen[T]: Encoder[T] = macro Magnolia.gen[T]
}
