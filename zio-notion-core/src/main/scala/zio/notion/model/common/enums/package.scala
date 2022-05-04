package zio.notion.model.common

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model
package object enums {
  implicit val config: CirceConfiguration = model.config
}
