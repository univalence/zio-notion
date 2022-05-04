package zio.notion.model.common.rich_text

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enums.Color

@ConfiguredJsonCodec final case class Annotations(
    bold:          Boolean,
    italic:        Boolean,
    strikethrough: Boolean,
    underline:     Boolean,
    code:          Boolean,
    color:         Color
)
