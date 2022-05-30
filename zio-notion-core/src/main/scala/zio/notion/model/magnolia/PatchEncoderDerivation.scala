package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import magnolia1._

/**
 * A macro definition to deserialize database and page patch.
 *
 * This macro should respect several requirements, indeed properties
 * should:
 *   - appear if it is a patch
 *   - be null if it is a remove
 *   - disappear if it is an ignore or a none
 *
 * By default we can ask Circe to either remove all nulls or to keep
 * them but here we need both.
 *
 * __Example:__
 * {{{
 * case class Patch(foo: Int, bar: Option[Int], baz, Removable[Int], qux: Removable[Int])
 *
 * // {
 * //   "bar": 1,
 * //   "qux": null
 * // }
 * val patch = Patch(1, None, Ignore, Remove)
 * }}}
 */
object PatchEncoderDerivation extends EncoderDerivation {

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) => {
      val values: Seq[(String, Json)] =
        ctx.parameters
          .map(parameter => snakeCaseTransformation(parameter.label) -> parameter.typeclass.apply(parameter.dereference(value)))
          .filterNot {
            case (key, _) if Set("page", "database")(key)                      => true // page and database key should be dropped
            case (_, json) if json.isNull                                      => true // Ignore and None should be dropped
            case (_, json) if json.isObject && json.asObject.forall(_.isEmpty) => true // Empty map should be dropped
            case _                                                             => false
          }
          .map {
            case (key, json) if json.isArray && json.asArray.forall(_.isEmpty) => key -> Json.Null // Remove should be converted to null
            case pair                                                          => pair
          }

      Json.obj(values: _*)
    }
}
