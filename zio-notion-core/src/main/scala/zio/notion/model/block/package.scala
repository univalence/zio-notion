package zio.notion.model

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model
package object block {
  implicit val config: CirceConfiguration = model.config
}
