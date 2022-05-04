package zio.notion.model.database.property_descriptions

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enums.RollupFunction

object RollupDescription {
  @ConfiguredJsonCodec final case class Rollup(
      rollupPropertyName:   String,
      relationPropertyName: String,
      rollupPropertyId:     String,
      relationPropertyId:   String,
      function:             RollupFunction
  )
}
