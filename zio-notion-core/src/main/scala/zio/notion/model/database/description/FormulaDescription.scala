package zio.notion.model.database.description

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class FormulaDescription(expression: String)
