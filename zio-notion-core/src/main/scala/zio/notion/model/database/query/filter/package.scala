package zio.notion.model.database.query

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model
package object filter {
  implicit val config: CirceConfiguration = model.config
}
