package zio.notion

import io.circe.Printer
import io.circe.generic.extras.{Configuration => CirceConfiguration}

package object model {
  implicit val config: CirceConfiguration = CirceConfiguration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames.withDiscriminator("type")

  val printer: Printer = Printer.spaces2
}
