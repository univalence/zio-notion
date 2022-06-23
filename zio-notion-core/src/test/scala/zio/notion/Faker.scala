package zio.notion

import zio.notion.Faker.FakeProperty.{fakeCheckbox, fakeTitle}
import zio.notion.model.block.{Block, BlockContent}
import zio.notion.model.block.BlockContent._
import zio.notion.model.common
import zio.notion.model.common.{Icon, Id, Parent}
import zio.notion.model.common.enumeration.{Color, Language}
import zio.notion.model.common.richtext.{Annotations, RichTextFragment}
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

  val emptyPage: Page =
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
      properties     = Map.empty,
      url            = fakeUrl
    )

  val fakePage: Page = emptyPage.copy(properties = Map("Test" -> fakeTitle, "Checkbox" -> fakeCheckbox))

  val fakeDatabase: Database =
    Database(
      createdTime    = fakeDatetime,
      lastEditedTime = fakeDatetime,
      createdBy      = Id(fakeUUID),
      lastEditedBy   = Id(fakeUUID),
      id             = fakeUUID,
      title          = List(RichTextFragment.default("test", Annotations.default)),
      cover          = None,
      icon           = None,
      parent         = Parent.Workspace,
      archived       = false,
      properties     = Map("Test" -> PropertyDefinition.CreatedTime("id", "Test")),
      url            = fakeUrl
    )

  val fakeBlock: Block =
    Block(
      "id",
      createdTime    = fakeDatetime,
      lastEditedTime = fakeDatetime,
      createdBy      = Id(fakeUUID),
      lastEditedBy   = Id(fakeUUID),
      hasChildren    = true,
      archived       = true,
      content        = BlockContent.ToDo(Seq.empty, checked = false, Color.Blue)
    )

  val fakePropertyDefinitions: Map[String, PropertySchema.Title.type] = Map("Name" -> PropertySchema.Title)

  object FakeBlock {
    val fakeParagraph: Paragraph               = Paragraph(Seq.empty, Color.Default)
    val fakeHeadingOne: HeadingOne             = HeadingOne(Seq.empty, Color.Default)
    val fakeHeadingTwo: HeadingTwo             = HeadingTwo(Seq.empty, Color.Default)
    val fakeHeadingThree: HeadingThree         = HeadingThree(Seq.empty, Color.Default)
    val fakeCallout: Callout                   = Callout(Seq.empty, Icon.Emoji(fakeEmoji), Color.Default)
    val fakeQuote: Quote                       = Quote(Seq.empty, Color.Default)
    val fakeBulletedListItem: BulletedListItem = BulletedListItem(Seq.empty, Color.Default)
    val fakeNumberedListItem: NumberedListItem = NumberedListItem(Seq.empty, Color.Default)
    val fakeToggle: Toggle                     = Toggle(Seq.empty, Color.Default)
    val fakeCode: Code                         = Code(Seq.empty, Language.Scala)
    val fakeChildPage: ChildPage               = ChildPage("page")
    val fakeChildDatabase: ChildDatabase       = ChildDatabase("database")
    val fakeEmbed: Embed                       = Embed(fakeUrl)
    val fakeImage: Image                       = Image(common.File.External(fakeUrl))
    val fakeVideo: Video                       = Video(common.File.External(fakeUrl))
    val fakeFile: File                         = File(common.File.External(fakeUrl))
    val fakePdf: Pdf                           = Pdf(common.File.External(fakeUrl))
    val fakeBookmark: Bookmark                 = Bookmark(fakeUrl)
    val fakeEquation: Equation                 = Equation("e=mc^2")
    val fakeTableOfContents: TableOfContents   = TableOfContents(Color.Default)
    val fakeColumn: Column                     = Column(Seq(fakeEquation, fakeFile))
    val fakeColumnList: ColumnList             = ColumnList(Seq(fakeColumn, fakeColumn))
    val fakeLinkPreview: LinkPreview           = LinkPreview(fakeUrl)
    val fakeTemplate: Template                 = Template(Seq.empty, Seq(fakePdf))
    val fakeLinkToPage: LinkToPage             = LinkToPage(LinkToPage.LinkType.Database, fakeUUID)
  }

  object FakeProperty {

    val fakeTitle: Title = Title("abc", List(RichTextFragment.default("test", Annotations.default)))

    val fakeDate: Date = Date("abc", None)

    val fakeCheckbox: Checkbox = Checkbox("def", Some(false))
  }

  object FakePatchedProperty {

    val fakePatchedTitle: PatchedTitle   = PatchedTitle(List(RichTextFragment.default("test", Annotations.default)))
    val fakePatchedNumber: PatchedNumber = PatchedNumber(85)

  }
}
