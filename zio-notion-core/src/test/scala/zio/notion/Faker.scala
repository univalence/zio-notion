package zio.notion

import java.time.{OffsetDateTime, ZoneOffset}

object Faker {
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
