package zio.notion.model.database.metadata

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.RollupFunction

@ConfiguredJsonCodec final case class RollupMetadata(
    rollupPropertyName:   String,
    relationPropertyName: String,
    rollupPropertyId:     String,
    relationPropertyId:   String,
    function:             RollupFunction
)
