package zio.notion.model.page

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model
package object properties {
  implicit val config: CirceConfiguration = model.config
}
