package zio.notion.model.database.description

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.RollupFunction

object RollupDescription {
  @ConfiguredJsonCodec final case class Rollup(
      rollupPropertyName:   String,
      relationPropertyName: String,
      rollupPropertyId:     String,
      relationPropertyId:   String,
      function:             RollupFunction
  )
}
