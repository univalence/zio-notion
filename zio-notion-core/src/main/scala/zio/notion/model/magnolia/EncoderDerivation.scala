package zio.notion.model.magnolia

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import io.circe.syntax.EncoderOps
import magnolia1.{CaseClass, SealedTrait}

import zio.notion.utils.StringOps.notionify

/** A type discriminant based encoder derivation. */
object EncoderDerivation extends BaseEncoderDerivation {

  def split[T](ctx: SealedTrait[Encoder, T]): Encoder[T] =
    (value: T) =>
      ctx.split(value) { subtype =>
        val caseClassName = notionify(subtype.typeName.short)
        Json.obj(
          "type"        -> caseClassName.asJson,
          caseClassName -> subtype.typeclass(subtype.cast(value))
        )
      }

  def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
    (value: T) => {
      val values =
        ctx.parameters
          .map(parameter => snakeCaseTransformation(parameter.label) -> parameter.typeclass.apply(parameter.dereference(value)))

      Json.obj(values: _*)
    }
}
