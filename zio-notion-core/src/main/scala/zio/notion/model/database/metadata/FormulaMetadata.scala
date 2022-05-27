package zio.notion.model.database.metadata

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class FormulaMetadata(expression: String)
