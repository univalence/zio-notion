package zio.notion

import zio.notion.Faker.FakeProperty.{fakeCheckbox, fakeTitle}
import zio.notion.model.common.{Parent, UserId}
import zio.notion.model.page.Page
import zio.notion.model.page.property.Property.{Checkbox, Date, Title}

import java.time.{LocalDate, OffsetDateTime, ZoneOffset}
import java.util.UUID

object Faker {
  val fakeEmoji: String = "🎉"

  val fakeUUID: UUID = UUID.fromString("3868f708-ae46-461f-bfcf-72d34c9536f9")

  val fakeUrl: String = "https://notion.zio"

  val fakeName: String = "Name"

  val fakeEmail: String = "testsuite@univalence.io"

  val fakePhoneNumber: String = "+1-202-555-0164"

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

  val fakeLocalDate: LocalDate =
    LocalDate.of(
      2022,
      2,
      22
    )

  val fakePage: Page =
    Page(
      createdTime    = fakeDatetime,
      lastEditedTime = fakeDatetime,
      createdBy      = UserId(fakeUUID),
      lastEditedBy   = UserId(fakeUUID),
      id             = fakeUUID,
      cover          = None,
      icon           = None,
      parent         = Parent.Workspace,
      archived       = false,
      properties     = Map("Test" -> fakeTitle, "Checkbox" -> fakeCheckbox),
      url            = fakeUrl
    )

  object FakeProperty {
    val fakeTitle: Title = Title("abc", Title.defaultData("Test"))

    val fakeDate: Date = Date("abc", None)

    val fakeCheckbox: Checkbox = Checkbox("def", Some(false))
  }
}
