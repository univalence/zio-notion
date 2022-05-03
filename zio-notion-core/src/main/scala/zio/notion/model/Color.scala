package zio.notion.model

import io.circe._
import io.circe.generic.extras.semiauto.deriveEnumerationCodec

sealed trait Color
sealed trait BaseColor       extends Color
sealed trait BackgroundColor extends Color

object Color {
  final case object Default extends BaseColor
  final case object Gray    extends BaseColor
  final case object Brown   extends BaseColor
  final case object Orange  extends BaseColor
  final case object Yellow  extends BaseColor
  final case object Green   extends BaseColor
  final case object Blue    extends BaseColor
  final case object Purple  extends BaseColor
  final case object Pink    extends BaseColor
  final case object Red     extends BaseColor

  final case object GrayBackground   extends BackgroundColor
  final case object BrownBackground  extends BackgroundColor
  final case object OrangeBackground extends BackgroundColor
  final case object YellowBackground extends BackgroundColor
  final case object GreenBackground  extends BackgroundColor
  final case object BlueBackground   extends BackgroundColor
  final case object PurpleBackground extends BackgroundColor
  final case object PinkBackground   extends BackgroundColor
  final case object RedBackground    extends BackgroundColor

  // CirceConfiguration.default.withSnakeCaseConstructorNames

  implicit val codecBaseColor: Codec[BaseColor] = deriveEnumerationCodec[BaseColor]
  implicit val codecColor: Codec[Color]         = deriveEnumerationCodec[Color]
}
