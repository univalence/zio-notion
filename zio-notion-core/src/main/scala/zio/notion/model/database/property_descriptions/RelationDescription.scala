package zio.notion.model.database.property_descriptions

import io.circe.generic.extras.ConfiguredJsonCodec

object RelationDescription {
  @ConfiguredJsonCodec final case class Relation(databaseId: String, syncedPropertyId: String, syncedPropertyName: String)
}
