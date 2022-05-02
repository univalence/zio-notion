package zio.notion

import io.circe.generic.extras.{Configuration => CirceConfiguration}

package object model {
  implicit val config: CirceConfiguration =
    CirceConfiguration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames.withDiscriminator("type")
}
