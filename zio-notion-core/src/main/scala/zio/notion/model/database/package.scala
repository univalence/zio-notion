package zio.notion.model

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model

package object database {
  implicit val config: CirceConfiguration = model.config
}
