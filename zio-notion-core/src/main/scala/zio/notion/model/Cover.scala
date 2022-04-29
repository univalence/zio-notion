package zio.notion.model

import zio.json._
import zio.notion.model.{External => BaseExternal}
import zio.notion.model.{File => BaseFile}

@jsonDiscriminator("type") sealed trait Cover

object Cover {
  @jsonHint("external")
  case class External(external: BaseExternal) extends Cover

  @jsonHint("file")
  case class File(file: BaseFile) extends Cover

  implicit val decoder: JsonDecoder[Cover] = DeriveJsonDecoder.gen[Cover]
  implicit val encoder: JsonEncoder[Cover] = DeriveJsonEncoder.gen[Cover]
}
