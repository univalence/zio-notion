package zio.notion

import zio.Scope
import zio.json._
import zio.notion.model.Parent
import zio.test._
import zio.test.Assertion._

object NotionADTTest extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Test the ADT")(
      test("parent") {
        val raw: String =
          """{
            |   "type": "database_id",
            |   "database_id": "3868f708-ae46-461f-bfcf-72d34c9536f9"
            |}""".stripMargin

        val expected: Parent.Database = Parent.Database("3868f708-ae46-461f-bfcf-72d34c9536f9")

        assert(raw.fromJson[Parent])(isRight(equalTo(expected)))
      }
    )
}
