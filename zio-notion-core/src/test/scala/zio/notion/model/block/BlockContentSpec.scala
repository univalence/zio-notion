package zio.notion.model.block

import io.circe.jawn.decode
import io.circe.syntax.EncoderOps

import zio._
import zio.notion.Faker.fakeBlock
import zio.notion.Faker.FakeBlock._
import zio.notion.dsl.BlockContentContext
import zio.notion.model.block.BlockContent._
import zio.notion.model.printer
import zio.notion.utils.StringOps.notionify
import zio.test._

object BlockContentSpec extends ZIOSpecDefault {

  def testBlockContentDeserialization(content: BlockContent): Spec[Any, Nothing] = {
    val contentName: String = notionify(content.getClass.getSimpleName.split('$').head)
    val block               = fakeBlock.copy(content = content)

    test(s"We should be able to encode then decode again a block with a $contentName") {
      val json          = printer.print(block.asJson)
      val maybeNewBlock = decode[Block](json)
      assertTrue(maybeNewBlock == Right(block))
    }
  }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("BlockContent serde suite")(
      test("We should be able to parse any supported block content from json") {
        val expected =
          """{
            |  "object" : "block",
            |  "type" : "heading_1",
            |  "heading_1" : {
            |    "rich_text" : [
            |      {
            |        "text" : {
            |          "content" : "My title",
            |          "link" : null
            |        },
            |        "annotations" : {
            |          "bold" : false,
            |          "italic" : false,
            |          "strikethrough" : false,
            |          "underline" : false,
            |          "code" : false,
            |          "color" : "default"
            |        },
            |        "plain_text" : "My title",
            |        "href" : null,
            |        "type" : "text"
            |      }
            |    ],
            |    "color" : "default"
            |  }
            |}""".stripMargin

        val block: BlockContent = h1"My title"

        assertTrue(printer.print(block.asJson) == expected)
      },
      testBlockContentDeserialization(Unsupported),
      testBlockContentDeserialization(fakeParagraph),
      testBlockContentDeserialization(fakeHeadingOne),
      testBlockContentDeserialization(fakeHeadingTwo),
      testBlockContentDeserialization(fakeHeadingThree),
      testBlockContentDeserialization(fakeCallout),
      testBlockContentDeserialization(fakeQuote),
      testBlockContentDeserialization(fakeBulletedListItem),
      testBlockContentDeserialization(fakeNumberedListItem),
      testBlockContentDeserialization(fakeToggle),
      testBlockContentDeserialization(fakeCode),
      testBlockContentDeserialization(fakeChildPage),
      testBlockContentDeserialization(fakeChildDatabase),
      testBlockContentDeserialization(fakeEmbed),
      testBlockContentDeserialization(fakeImage),
      testBlockContentDeserialization(fakeVideo),
      testBlockContentDeserialization(fakeFile),
      testBlockContentDeserialization(fakePdf),
      testBlockContentDeserialization(fakeBookmark),
      testBlockContentDeserialization(fakeEquation),
      testBlockContentDeserialization(Divider),
      testBlockContentDeserialization(fakeTableOfContents),
      testBlockContentDeserialization(Breadcrumb),
      testBlockContentDeserialization(fakeColumn),
      testBlockContentDeserialization(fakeColumnList),
      testBlockContentDeserialization(fakeLinkPreview),
      testBlockContentDeserialization(fakeTemplate),
      testBlockContentDeserialization(fakeLinkToPage)
    )
}
