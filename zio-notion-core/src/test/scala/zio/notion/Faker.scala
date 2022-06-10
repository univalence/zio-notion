package zio.notion

import zio.notion.Faker.FakeProperty.{fakeCheckbox, fakeTitle}
import zio.notion.model.common.{Id, Parent}
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.database.{Database, PropertyDefinition}
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.page.Page
import zio.notion.model.page.PatchedProperty.{PatchedNumber, PatchedTitle}
import zio.notion.model.page.Property.{Checkbox, Date, Title}

import java.time.{LocalDate, OffsetDateTime, OffsetTime, ZoneOffset}

object Faker {
  val fakeEmoji: String = "🎉"

  val fakeUUID: String = "3868f708-ae46-461f-bfcf-72d34c9536f9"

  val fakeUrl: String = "https://notion.zio"

  val fakeName: String = "Name"

  val fakeEmail: String = "testsuite@univalence.io"

  val fakePhoneNumber: String = "+1-202-555-0164"

  val fakeDate: LocalDate = LocalDate.of(2022, 12, 24)

  val fakeDatetime: OffsetDateTime = fakeDate.atTime(OffsetTime.of(15, 10, 0, 0, ZoneOffset.UTC))

  val fakePage: Page =
    Page(
      createdTime    = fakeDatetime,
      lastEditedTime = fakeDatetime,
      createdBy      = Id(fakeUUID),
      lastEditedBy   = Id(fakeUUID),
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
      createdBy      = Id(fakeUUID),
      lastEditedBy   = Id(fakeUUID),
      id             = fakeUUID,
      title          = List(RichTextData.default("test", Annotations.default)),
      cover          = None,
      icon           = None,
      parent         = Parent.Workspace,
      archived       = false,
      properties     = Map("Test" -> PropertyDefinition.CreatedTime("id", "Test")),
      url            = fakeUrl
    )

  val fakePropertyDefinitions: Map[String, PropertySchema.Title.type] = Map("Name" -> PropertySchema.Title)

  object FakeProperty {

    val fakeTitle: Title = Title("abc", List(RichTextData.default("test", Annotations.default)))

    val fakeDate: Date = Date("abc", None)

    val fakeCheckbox: Checkbox = Checkbox("def", Some(false))
  }

  object FakePatchedProperty {

    val fakePatchedTitle: PatchedTitle   = PatchedTitle(List(RichTextData.default("test", Annotations.default)))
    val fakePatchedNumber: PatchedNumber = PatchedNumber(85)

  }
}
