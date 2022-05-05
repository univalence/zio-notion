package zio.notion.model.page.patch

import io.circe.{Encoder, Json}

sealed trait PatchedProperty

object PatchedProperty {
  case class Number(number: Double)

  object Number {
    implicit val encoder: Encoder[Number] =
      (number: Number) =>
        Json.obj(
          ("type", Json.JString("number")),
          (
            "number",
            Json.obj(
              ("number", Json.fromDouble(number.number).get)
            )
          )
        )
  }
}
