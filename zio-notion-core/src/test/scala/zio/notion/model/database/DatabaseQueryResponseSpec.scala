package zio.notion.model.database

import io.circe.parser.decode

import zio.Scope
import zio.test.{assert, Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.Assertion.isRight

object DatabaseQueryResponseSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("DB query response serde suite")(
      test("We should be able to parse a database query response json") {
        val json: String =
          """{
            |    "object": "list",
            |    "results": [
            |        {
            |            "object": "page",
            |            "id": "34fa828f-092c-41f4-a892-4a69f5902f3b",
            |            "created_time": "2022-05-03T14:32:00.000Z",
            |            "last_edited_time": "2022-05-10T08:39:00.000Z",
            |            "created_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "last_edited_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "cover": null,
            |            "icon": null,
            |            "parent": {
            |                "type": "database_id",
            |                "database_id": "678cf788-8470-4798-8336-a285836c9de6"
            |            },
            |            "archived": false,
            |            "properties": {
            |                "Tags": {
            |                    "id": "JLha",
            |                    "type": "checkbox",
            |                    "checkbox": true
            |                },
            |                "Name": {
            |                    "id": "title",
            |                    "type": "title",
            |                    "title": [
            |                        {
            |                            "type": "text",
            |                            "text": {
            |                                "content": "hello",
            |                                "link": null
            |                            },
            |                            "annotations": {
            |                                "bold": false,
            |                                "italic": false,
            |                                "strikethrough": false,
            |                                "underline": false,
            |                                "code": false,
            |                                "color": "default"
            |                            },
            |                            "plain_text": "hello",
            |                            "href": null
            |                        }
            |                    ]
            |                }
            |            },
            |            "url": "https://www.notion.so/hello-34fa828f092c41f4a8924a69f5902f3b"
            |        },
            |        {
            |            "object": "page",
            |            "id": "886dab2d-6344-4a09-8451-29fc30dcdd93",
            |            "created_time": "2022-05-03T14:32:00.000Z",
            |            "last_edited_time": "2022-05-10T08:39:00.000Z",
            |            "created_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "last_edited_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "cover": null,
            |            "icon": null,
            |            "parent": {
            |                "type": "database_id",
            |                "database_id": "678cf788-8470-4798-8336-a285836c9de6"
            |            },
            |            "archived": false,
            |            "properties": {
            |                "Tags": {
            |                    "id": "JLha",
            |                    "type": "checkbox",
            |                    "checkbox": false
            |                },
            |                "Name": {
            |                    "id": "title",
            |                    "type": "title",
            |                    "title": [
            |                        {
            |                            "type": "text",
            |                            "text": {
            |                                "content": "goodbye",
            |                                "link": null
            |                            },
            |                            "annotations": {
            |                                "bold": false,
            |                                "italic": false,
            |                                "strikethrough": false,
            |                                "underline": false,
            |                                "code": false,
            |                                "color": "default"
            |                            },
            |                            "plain_text": "goodbye",
            |                            "href": null
            |                        }
            |                    ]
            |                }
            |            },
            |            "url": "https://www.notion.so/goodbye-886dab2d63444a09845129fc30dcdd93"
            |        },
            |        {
            |            "object": "page",
            |            "id": "b03da2cd-cdbe-4235-b598-b63d1847eaf5",
            |            "created_time": "2022-05-03T14:32:00.000Z",
            |            "last_edited_time": "2022-05-10T08:39:00.000Z",
            |            "created_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "last_edited_by": {
            |                "object": "user",
            |                "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |            },
            |            "cover": null,
            |            "icon": null,
            |            "parent": {
            |                "type": "database_id",
            |                "database_id": "678cf788-8470-4798-8336-a285836c9de6"
            |            },
            |            "archived": false,
            |            "properties": {
            |                "Tags": {
            |                    "id": "JLha",
            |                    "type": "checkbox",
            |                    "checkbox": false
            |                },
            |                "Name": {
            |                    "id": "title",
            |                    "type": "title",
            |                    "title": [
            |                        {
            |                            "type": "text",
            |                            "text": {
            |                                "content": "test",
            |                                "link": null
            |                            },
            |                            "annotations": {
            |                                "bold": false,
            |                                "italic": false,
            |                                "strikethrough": false,
            |                                "underline": false,
            |                                "code": false,
            |                                "color": "default"
            |                            },
            |                            "plain_text": "test",
            |                            "href": null
            |                        }
            |                    ]
            |                }
            |            },
            |            "url": "https://www.notion.so/test-b03da2cdcdbe4235b598b63d1847eaf5"
            |        }
            |    ],
            |    "next_cursor": null,
            |    "has_more": false,
            |    "type": "page",
            |    "page": {}
            |}""".stripMargin

        assert(decode[DatabaseQuery](json))(isRight)
      }
    )
}
