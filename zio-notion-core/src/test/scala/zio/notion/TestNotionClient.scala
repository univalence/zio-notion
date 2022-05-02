package zio.notion

import sttp.client3.Response

import zio._
import zio.notion.NotionClient.NotionResponse

final case class TestNotionClient() extends NotionClient {
  override def retrievePage(pageId: String): IO[NotionError, NotionResponse] =
    ZIO.succeed {
      Response.ok(
        Right(
          s"""{
             |    "object": "page",
             |    "id": "$pageId",
             |    "created_time": "2022-01-28T10:47:00.000Z",
             |    "last_edited_time": "2022-02-22T08:30:00.000Z",
             |    "created_by": {
             |        "object": "user",
             |        "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71"
             |    },
             |    "last_edited_by": {
             |        "object": "user",
             |        "id": "e20722e7-0439-4122-aa99-8304e561bc1d"
             |    },
             |    "cover": null,
             |    "icon": null,
             |    "parent": {
             |        "type": "database_id",
             |        "database_id": "3868f708-ae46-461f-bfcf-72d34c9536f9"
             |    },
             |    "archived": false,
             |    "properties": {
             |        "Erreurs": {
             |            "id": ">QlB",
             |            "type": "rich_text",
             |            "rich_text": []
             |        },
             |        "Status": {
             |            "id": "@Gcb",
             |            "type": "select",
             |            "select": {
             |                "id": "150fbaa5-7b63-4903-855c-c6973cf0ac48",
             |                "name": "Posted",
             |                "color": "green"
             |            }
             |        },
             |        "Date de publication": {
             |            "id": "DY:@",
             |            "type": "date",
             |            "date": {
             |                "start": "2022-02-22",
             |                "end": null,
             |                "time_zone": null
             |            }
             |        },
             |        "Reviewed by": {
             |            "id": "T[X?",
             |            "type": "people",
             |            "people": []
             |        },
             |        "Mots clés": {
             |            "id": "Xz}B",
             |            "type": "multi_select",
             |            "multi_select": [
             |                {
             |                    "id": "ee7d5ee6-6df1-455d-b0c3-4aee64a0bc63",
             |                    "name": "scala2",
             |                    "color": "brown"
             |                }
             |            ]
             |        },
             |        "Link": {
             |            "id": "jJz@",
             |            "type": "rich_text",
             |            "rich_text": [
             |                {
             |                    "type": "text",
             |                    "text": {
             |                        "content": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
             |                        "link": {
             |                            "url": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
             |                        }
             |                    },
             |                    "annotations": {
             |                        "bold": false,
             |                        "italic": false,
             |                        "strikethrough": false,
             |                        "underline": false,
             |                        "code": false,
             |                        "color": "default"
             |                    },
             |                    "plain_text": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
             |                    "href": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
             |                }
             |            ]
             |        },
             |        "Auteur": {
             |            "id": "pJG_",
             |            "type": "people",
             |            "people": [
             |                {
             |                    "object": "user",
             |                    "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71",
             |                    "name": "Dylan DO AMARAL",
             |                    "avatar_url": "https://lh3.googleusercontent.com/-6B9dSBYVuvk/AAAAAAAAAAI/AAAAAAAAAAc/su4if5KvrbA/photo.jpg",
             |                    "type": "person",
             |                    "person": {}
             |                }
             |            ]
             |        },
             |        "Type": {
             |            "id": "|=~Q",
             |            "type": "select",
             |            "select": {
             |                "id": "01f5896b-8c7c-4c6c-a76f-c1c2eb6d12c9",
             |                "name": "Tips",
             |                "color": "purple"
             |            }
             |        },
             |        "Name": {
             |            "id": "title",
             |            "type": "title",
             |            "title": [
             |                {
             |                    "type": "text",
             |                    "text": {
             |                        "content": "Les énumérations en Scala 2.X",
             |                        "link": null
             |                    },
             |                    "annotations": {
             |                        "bold": false,
             |                        "italic": false,
             |                        "strikethrough": false,
             |                        "underline": false,
             |                        "code": false,
             |                        "color": "default"
             |                    },
             |                    "plain_text": "Les énumérations en Scala 2.X",
             |                    "href": null
             |                }
             |            ]
             |        }
             |    },
             |    "url": "https://www.notion.so/Les-num-rations-en-Scala-2-X-1c2d0a80332146419615f345185de05a"
             |}""".stripMargin
        )
      )
    }
}

object TestNotionClient {
  val layer: ULayer[NotionClient] = ZLayer.succeed(TestNotionClient())
}
