package zio.notion

import io.circe.JsonObject

import zio.notion.Faker.FakeProperty.{fakeCheckbox, fakeTitle}
import zio.notion.model.common.{Parent, UserId}
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.database.Database
import zio.notion.model.database.description.PropertyDescription
import zio.notion.model.page.Page
import zio.notion.model.page.property.Property.{Checkbox, Date, Title}

import java.time.{LocalDate, OffsetDateTime, ZoneOffset}

object Faker {
  val fakeEmoji: String = "🎉"

  val fakeUUID: String = "3868f708-ae46-461f-bfcf-72d34c9536f9"

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

  val fakeDatabase: Database =
    Database(
      createdTime    = fakeDatetime,
      lastEditedTime = fakeDatetime,
      createdBy      = UserId(fakeUUID),
      lastEditedBy   = UserId(fakeUUID),
      id             = fakeUUID,
      title          = List(RichTextData.default("test", Annotations.default)),
      cover          = None,
      icon           = None,
      parent         = Parent.Workspace,
      archived       = false,
      properties     = Map("Test" -> PropertyDescription.CreatedTime("id", "Test", JsonObject())),
      url            = fakeUrl
    )

  object FakeProperty {
    val fakeTitle: Title = Title("abc", List(RichTextData.default("test", Annotations.default)))

    val fakeDate: Date = Date("abc", None)

    val fakeCheckbox: Checkbox = Checkbox("def", Some(false))
  }
}
