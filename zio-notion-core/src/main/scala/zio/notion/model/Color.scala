package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

object Color {
  case object Default          extends BaseColor
  case object Gray             extends BaseColor
  case object Brown            extends BaseColor
  case object Orange           extends BaseColor
  case object Yellow           extends BaseColor
  case object Green            extends BaseColor
  case object Blue             extends BaseColor
  case object Purple           extends BaseColor
  case object Pink             extends BaseColor
  case object Red              extends BaseColor
  case object GrayBackground   extends BackgroundColor
  case object BrownBackground  extends BackgroundColor
  case object OrangeBackground extends BackgroundColor
  case object YellowBackground extends BackgroundColor
  case object GreenBackground  extends BackgroundColor
  case object BlueBackground   extends BackgroundColor
  case object PurpleBackground extends BackgroundColor
  case object PinkBackground   extends BackgroundColor
  case object RedBackground    extends BackgroundColor
}

@ConfiguredJsonCodec sealed trait Color
sealed trait BaseColor       extends Color
sealed trait BackgroundColor extends Color
