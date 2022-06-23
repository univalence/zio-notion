package zio.notion.model.common

import io.circe._
import io.circe.Decoder.Result
import io.circe.generic.extras._
import io.circe.syntax.EncoderOps

import zio.notion.model.magnolia.NoDiscriminantNoNullEncoderDerivation

import java.time.OffsetDateTime

sealed trait File

object File {

  @ConfiguredJsonCodec final case class External(url: String)                             extends File
  @ConfiguredJsonCodec final case class Internal(url: String, expiryTime: OffsetDateTime) extends File

  implicit val codec: Codec[File] =
    new Codec[File] {

      override def apply(c: HCursor): Result[File] =
        for {
          fileType <- c.downField("type").as[String]
          file <-
            fileType match {
              case "external" => c.downField("external").as[External]
              case "file"     => c.downField("file").as[Internal]
              case v          => Left(DecodingFailure(s"The type '$v' is unknown for file", c.history))
            }
        } yield file

      override def apply(file: File): Json = {
        val name =
          file match {
            case _: External => "external"
            case _: Internal => "file"
          }

        Json.obj(
          "type" -> name.asJson,
          name   -> NoDiscriminantNoNullEncoderDerivation.gen[File].apply(file)
        )
      }
    }
}
