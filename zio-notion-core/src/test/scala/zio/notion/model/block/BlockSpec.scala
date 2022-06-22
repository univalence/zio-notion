package zio.notion.model.block

import io.circe.jawn.decode
import io.circe.syntax.EncoderOps

import zio._
import zio.notion.Faker.FakeBlock._
import zio.notion.Faker.fakeBlock
import zio.notion.model.block.BlockContent._
import zio.notion.model.printer
import zio.notion.utils.StringOps.notionify
import zio.test._

object BlockSpec extends ZIOSpecDefault {

  def testBlockContentDeserialization(content: BlockContent): Spec[Any, Nothing] = {
    val contentName: String = notionify(content.getClass.getSimpleName)
    val block               = fakeBlock.copy(content = content)

    test(s"We should be able to encode then decode again a block with a $contentName") {
      val json          = printer.print(block.asJson)
      val maybeNewBlock = decode[Block](json)
      assertTrue(maybeNewBlock == Right(block))
    }
  }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Block serde suite")(
      test("We should be able to encode a block") {
        val expected =
          """{
            |  "id" : "id",
            |  "created_time" : "2022-12-24T15:10:00Z",
            |  "last_edited_time" : "2022-12-24T15:10:00Z",
            |  "created_by" : {
            |    "id" : "3868f708-ae46-461f-bfcf-72d34c9536f9"
            |  },
            |  "last_edited_by" : {
            |    "id" : "3868f708-ae46-461f-bfcf-72d34c9536f9"
            |  },
            |  "archived" : true,
            |  "has_children" : true,
            |  "type" : "to_do",
            |  "to_do" : {
            |    "rich_text" : [
            |    ],
            |    "checked" : false,
            |    "color" : "blue",
            |    "children" : [
            |    ]
            |  }
            |}""".stripMargin

        assertTrue(printer.print(fakeBlock.asJson) == expected)
      },
      test("We should be able to decode a block") {
        val json =
          """{
            |  "object": "block",
            |  "id": "9bc30ad4-9373-46a5-84ab-0a7845ee52e6",
            |  "created_time": "2021-03-16T16:31:00.000Z",
            |  "created_by": {
            |    "object": "user",
            |    "id": "cb38e95d-00cf-4e7e-adce-974f4a44a547"
            |  },
            |  "last_edited_time": "2021-03-16T16:32:00.000Z",
            |  "last_edited_by": {
            |    "object": "user",
            |    "id": "e79a0b74-3aba-4149-9f74-0bb5791a6ee6"
            |  },
            |  "has_children": false,
            |  "type": "to_do",
            |  "archived": false,
            |  "to_do": {
            |    "rich_text": [
            |      {
            |        "type": "text",
            |        "text": {
            |          "content": "Lacinato kale",
            |          "link": null
            |        },
            |        "annotations": {
            |          "bold": false,
            |          "italic": false,
            |          "strikethrough": false,
            |          "underline": false,
            |          "code": false,
            |          "color": "default"
            |        },
            |        "plain_text": "Lacinato kale",
            |        "href": null
            |      }
            |    ],
            |    "checked": true,
            |    "color": "default"
            |  }
            |}""".stripMargin

        ZIO
          .fromEither(decode[Block](json))
          .map(block =>
            assertTrue(block.id == "9bc30ad4-9373-46a5-84ab-0a7845ee52e6") &&
              assertTrue(block.content.asInstanceOf[ToDo].checked) &&
              assertTrue(!block.archived)
          )
      },
      test("We should be able to parse any supported block content from json") {
        val json =
          """{
            |  "object": "block",
            |  "id": "9bc30ad4-9373-46a5-84ab-0a7845ee52e6",
            |  "created_time": "2021-03-16T16:31:00.000Z",
            |  "created_by": {
            |    "object": "user",
            |    "id": "cb38e95d-00cf-4e7e-adce-974f4a44a547"
            |  },
            |  "last_edited_time": "2021-03-16T16:32:00.000Z",
            |  "last_edited_by": {
            |    "object": "user",
            |    "id": "e79a0b74-3aba-4149-9f74-0bb5791a6ee6"
            |  },
            |  "has_children": false,
            |  "type": "to_do",
            |  "archived": false,
            |  "to_do": {
            |    "rich_text": [
            |      {
            |        "type": "text",
            |        "text": {
            |          "content": "Lacinato kale",
            |          "link": null
            |        },
            |        "annotations": {
            |          "bold": false,
            |          "italic": false,
            |          "strikethrough": false,
            |          "underline": false,
            |          "code": false,
            |          "color": "default"
            |        },
            |        "plain_text": "Lacinato kale",
            |        "href": null
            |      }
            |    ],
            |    "checked": true,
            |    "color": "default"
            |  }
            |}""".stripMargin

        ZIO
          .fromEither(decode[Block](json))
          .map(block =>
            assertTrue(block.id == "9bc30ad4-9373-46a5-84ab-0a7845ee52e6") &&
              assertTrue(block.content.asInstanceOf[ToDo].checked) &&
              assertTrue(!block.archived)
          )
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
      testBlockContentDeserialization(fakeColumnList)
    )
}
