package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import magnolia1._

/**
 * A macro definition to deserialize:
 * https://developers.notion.com/reference/property-value-object#multi-select-property-values
 */
object PatchEncoderDerivation {
  type Typeclass[T] = Encoder[T]

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.parameters.length match {
        case 1 =>
          val parameter = ctx.parameters.head
          Json.obj((snakeCaseTransformation(parameter.label), parameter.typeclass.apply(parameter.dereference(value))))
        case _ =>
          val name = snakeCaseTransformation(ctx.typeName.short.replace("Patched", ""))

          val properties =
            ctx.parameters
              .foldLeft(List.empty[(String, Json)])((acc, curr) =>
                acc :+ (snakeCaseTransformation(curr.label), curr.typeclass.apply(curr.dereference(value)))
              )
              .filter { case (_, json) => !json.isNull }

          Json.obj(name -> Json.obj(properties: _*))
      }

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.split(value) { sub =>
        sub.typeclass.apply(sub.cast(value))
      }

  implicit def gen[T]: Encoder[T] = macro Magnolia.gen[T]
}
