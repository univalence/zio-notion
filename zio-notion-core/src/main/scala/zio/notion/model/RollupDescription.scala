package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

object RollupDescription {
  @ConfiguredJsonCodec final case class Rollup(
      rollupPropertyName:   String,
      relationPropertyName: String,
      rollupPropertyId:     String,
      relationPropertyId:   String,
      function:             RollupFunction
  )

}
