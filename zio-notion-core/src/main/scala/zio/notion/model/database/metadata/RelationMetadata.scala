package zio.notion.model.database.metadata

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class RelationMetadata(databaseId: String, syncedPropertyId: String, syncedPropertyName: String)
