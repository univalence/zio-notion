package zio.notion.model

import io.circe.generic.extras._

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
