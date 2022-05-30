package zio.notion.model.page.property

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model

package object data {
  implicit val config: CirceConfiguration = model.config
}
