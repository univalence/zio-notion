package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import magnolia1._

/**
 * A macro encoder that ignores both null values and discriminant.
 *
 * Example:
 * {{{
 * sealed trait Foo
 * case class Bar(foo: Int, bar: Option[Int]) extends Foo
 *
 * // {
 * //   "foo": 1
 * // }
 * val bar = Bar(1, None)
 * }}}
 */
object NoDiscriminantNoNullEncoderDerivation {
  type Typeclass[T] = Encoder[T]

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) => {
      val values =
        ctx.parameters
          .map(parameter => snakeCaseTransformation(parameter.label) -> parameter.typeclass.apply(parameter.dereference(value)))
          .filterNot { case (_, json) => json.isNull }

      Json.obj(values: _*)
    }

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.split(value) { sub =>
        sub.typeclass.apply(sub.cast(value))
      }

  implicit def gen[T]: Encoder[T] = macro Magnolia.gen[T]
}
