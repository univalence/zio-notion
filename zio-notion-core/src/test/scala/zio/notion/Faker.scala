package zio.notion

import java.time.{OffsetDateTime, ZoneOffset}

object Faker {
  val fakeEmoji: String = "🎉"

  val fakeUUID: String = "3868f708-ae46-461f-bfcf-72d34c9536f9"

  val fakeUrl: String = "https://notion.zio"

  val fakeDatetime: OffsetDateTime =
    OffsetDateTime.of(
      2022,
      12,
      24,
      15,
      10,
      0,
      0,
      ZoneOffset.UTC
    )
}
