package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import magnolia1._

/**
 * A macro definition to deserialize:
 *   - [[https://developers.notion.com/reference/property-value-object#multi-select-property-values]]
 *   - [[https://developers.notion.com/reference/post-database-query]]
 *
 * __Example:__
 * {{{
 * case class Foo(bar: Int, baz: Int)
 *
 * // {
 * //   "foo": {
 * //     "bar": 1,
 * //     "baz": 2
 * //   }
 * // }
 * val foo = Foo(1, 2)
 * }}}
 */
object PropertyTypeEncoderDerivation extends EncoderDerivation {

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.parameters.length match {
        case _ =>
          val name = snakeCaseTransformation(ctx.typeName.short)

          val properties =
            ctx.parameters
              .foldLeft(List.empty[(String, Json)])((acc, curr) =>
                acc :+ (snakeCaseTransformation(curr.label) -> curr.typeclass.apply(curr.dereference(value)))
              )
              .filter { case (_, json) => !json.isNull }

          Json.obj(name -> Json.obj(properties: _*))
      }
}
