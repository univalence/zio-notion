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

object Annotations {
  val default: Annotations =
    Annotations(
      bold          = false,
      italic        = false,
      strikethrough = false,
      underline     = false,
      code          = false,
      Color.Default
    )
}
