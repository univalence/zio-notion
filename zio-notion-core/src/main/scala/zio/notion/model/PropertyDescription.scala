package zio.notion.model

import io.circe.JsonObject
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait PropertyDescription

object PropertyDescription {
  type EmptyObject = JsonObject

  final case class Title(id: String, name: String, title: EmptyObject)   extends PropertyDescription
  final case class People(id: String, name: String, people: EmptyObject) extends PropertyDescription

}
