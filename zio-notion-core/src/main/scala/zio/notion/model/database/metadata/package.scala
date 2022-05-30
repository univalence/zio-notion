package zio.notion.model.database

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model

package object metadata {
  implicit val config: CirceConfiguration = model.config
}
