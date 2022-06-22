package zio.notion.model

import io.circe.generic.extras.{Configuration => CirceConfiguration}

import zio.notion.model
import zio.notion.utils.StringOps.notionify

package object block {
  implicit val config: CirceConfiguration = model.config.withDefaults.copy(transformConstructorNames = notionify)
}
