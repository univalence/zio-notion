package zio.notion.model.magnolia

import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.Decoder.Result
import magnolia1._

import zio.notion.utils.StringOps.notionify

/** A type discriminant based decoder derivation. */
object DecoderDerivation {

  type Typeclass[T] = Decoder[T]
  implicit def gen[T]: Decoder[T] = macro Magnolia.gen[T]

  def split[T](ctx: SealedTrait[Decoder, T]): Decoder[T] =
    (cursor: HCursor) =>
      for {
        discriminant <- cursor.downField("type").as[String]
        typeclass <-
          ctx.subtypes.find(subtype => notionify(subtype.typeName.short) == discriminant) match {
            case Some(value) => Right(value.typeclass)
            case None        => Left(DecodingFailure(s"The type '$discriminant' does not exist", cursor.history))
          }
        t <- typeclass.tryDecode(cursor.downField(discriminant))
      } yield t

  def join[T](ctx: CaseClass[Decoder, T]): Decoder[T] =
    (c: HCursor) => {
      val initialization: Result[Seq[Any]] = Right(Seq.empty)

      val maybeParameters: Result[Seq[Any]] =
        ctx.parameters.foldLeft(initialization) { (acc, parameter) =>
          val maybeValue          = c.downField(notionify(parameter.label)).as[parameter.PType](parameter.typeclass)
          val maybeValueOrDefault = parameter.default.fold(maybeValue)(default => maybeValue.orElse(Right(default)))

          acc.flatMap(seq => maybeValueOrDefault.map(value => seq :+ value))
        }

      maybeParameters.map(ctx.rawConstruct)
    }

}
