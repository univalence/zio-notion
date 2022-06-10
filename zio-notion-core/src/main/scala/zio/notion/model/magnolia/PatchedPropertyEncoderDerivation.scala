package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import magnolia1._

/**
 * A macro definition to deserialize:
 *   - [[https://developers.notion.com/reference/property-value-object#multi-select-property-values]]
 *   - [[https://developers.notion.com/reference/post-database-query]]
 *
 * __Example 1 (one parameter):__
 * {{{
 * case class PatchedFoo(bar: Int)
 *
 * // {
 * //   "bar": 1
 * // }
 * val foo = PatchedFoo(1)
 * }}}
 *
 * __Example 2 (more than one parameter):__
 * {{{
 * case class PatchedFoo(bar: Int, baz: Int)
 *
 * // {
 * //   "foo": {
 * //     "bar": 1,
 * //     "baz": 2
 * //   }
 * // }
 * val foo = PatchedFoo(1, 2)
 * }}}
 */
object PatchedPropertyEncoderDerivation extends EncoderDerivation {

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.parameters.length match {
        case 0 =>
          Json.obj((snakeCaseTransformation(ctx.typeName.short), Json.obj()))
        case 1 =>
          val parameter = ctx.parameters.head
          Json.obj(snakeCaseTransformation(parameter.label) -> parameter.typeclass.apply(parameter.dereference(value)))
        case _ =>
          val classNameTransformed: String =
            ctx.typeName.short
              .replace("Patched", "")
              .replace("DateTime", "Date")
          val propertyType: String = snakeCaseTransformation(classNameTransformed)

          val properties =
            ctx.parameters
              .foldLeft(List.empty[(String, Json)])((acc, curr) =>
                acc :+ (snakeCaseTransformation(curr.label) -> curr.typeclass.apply(curr.dereference(value)))
              )
              .filter { case (_, json) => !json.isNull }

          Json.obj(propertyType -> Json.obj(properties: _*))
      }
}
