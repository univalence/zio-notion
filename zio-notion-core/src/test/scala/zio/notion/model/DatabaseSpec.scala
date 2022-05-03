package zio.notion.model

import io.circe
import io.circe.parser.decode
import zio.Scope
import zio.test._
import zio.test.Assertion.isRight

object DatabaseSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("DB serde suite")(
      test("We should be able to parse a database json") {
        val json: String =
          """
            |{
            |    "object": "database",
            |    "id": "7caa81b5-6383-4270-b89c-c386a9f0de13",
            |    "cover": null,
            |    "icon": null,
            |    "created_time": "2022-03-28T07:05:00.000Z",
            |    "created_by": {
            |        "object": "user",
            |        "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |    },
            |    "last_edited_by": {
            |        "object": "user",
            |        "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |    },
            |    "last_edited_time": "2022-04-12T14:41:00.000Z",
            |    "title": [
            |        {
            |            "type": "text",
            |            "text": {
            |                "content": "Pages",
            |                "link": null
            |            },
            |            "annotations": {
            |                "bold": false,
            |                "italic": false,
            |                "strikethrough": false,
            |                "underline": false,
            |                "code": false,
            |                "color": "default"
            |            },
            |            "plain_text": "Pages",
            |            "href": null
            |        }
            |    ],
            |    "properties": {
            |        "people": {
            |            "id": "wWmU",
            |            "name": "people",
            |            "type": "people",
            |            "people": {}
            |        },
            |        "name": {
            |            "id": "title",
            |            "name": "name",
            |            "type": "title",
            |            "title": {}
            |        }
            |    },
            |    "parent": {
            |        "type": "page_id",
            |        "page_id": "a43aaa9d-601d-4d74-802a-f5681db935e1"
            |    },
            |    "url": "https://www.notion.so/7caa81b563834270b89cc386a9f0de13",
            |    "archived": false
            |}
            |""".stripMargin

        assert(decode[Database](json))(isRight)
      }
    )
}