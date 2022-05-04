package zio.notion.model.database.property_descriptions

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class FormulaDescription(expression: String)
