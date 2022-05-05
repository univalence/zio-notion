package zio.notion.model.common.richtext

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.Color

@ConfiguredJsonCodec final case class Annotations(
    bold:          Boolean,
    italic:        Boolean,
    strikethrough: Boolean,
    underline:     Boolean,
    code:          Boolean,
    color:         Color
)
