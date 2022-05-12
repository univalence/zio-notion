package zio.notion.model.database

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model

package object patch {
  implicit val config: CirceConfiguration = model.config
}
