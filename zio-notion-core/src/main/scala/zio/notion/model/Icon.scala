package zio.notion.model

import zio.json._
import zio.notion.model.{External => BaseExternal, File => BaseFile}

@jsonDiscriminator("type") sealed trait Icon

object Icon {
  @jsonHint("emoji")
  case class Emoji(emoji: String) extends Icon

  @jsonHint("external")
  case class External(external: BaseExternal) extends Icon

  @jsonHint("file")
  case class File(file: BaseFile) extends Icon

  implicit val decoder: JsonDecoder[Icon] = DeriveJsonDecoder.gen[Icon]
  implicit val encoder: JsonEncoder[Icon] = DeriveJsonEncoder.gen[Icon]
}
