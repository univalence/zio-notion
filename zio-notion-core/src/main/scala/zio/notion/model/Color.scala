package zio.notion.model

import zio.json._

object Color {
  @jsonHint("default") case object Default                    extends BaseColor
  @jsonHint("gray") case object Gray                          extends BaseColor
  @jsonHint("brown") case object Brown                        extends BaseColor
  @jsonHint("orange") case object Orange                      extends BaseColor
  @jsonHint("yellow") case object Yellow                      extends BaseColor
  @jsonHint("green") case object Green                        extends BaseColor
  @jsonHint("blue") case object Blue                          extends BaseColor
  @jsonHint("purple") case object Purple                      extends BaseColor
  @jsonHint("pink") case object Pink                          extends BaseColor
  @jsonHint("red") case object Red                            extends BaseColor
  @jsonHint("gray_background") case object GrayBackground     extends BackgroundColor
  @jsonHint("brown_background") case object BrownBackground   extends BackgroundColor
  @jsonHint("orange_background") case object OrangeBackground extends BackgroundColor
  @jsonHint("yellow_background") case object YellowBackground extends BackgroundColor
  @jsonHint("green_background") case object GreenBackground   extends BackgroundColor
  @jsonHint("blue_background") case object BlueBackground     extends BackgroundColor
  @jsonHint("purple_background") case object PurpleBackground extends BackgroundColor
  @jsonHint("pink_background") case object PinkBackground     extends BackgroundColor
  @jsonHint("red_background") case object RedBackground       extends BackgroundColor

  implicit val decoder: JsonDecoder[Color] = DeriveJsonDecoder.gen[Color]
  implicit val encoder: JsonEncoder[Color] = DeriveJsonEncoder.gen[Color]
}

sealed trait Color
sealed trait BaseColor       extends Color
sealed trait BackgroundColor extends Color
