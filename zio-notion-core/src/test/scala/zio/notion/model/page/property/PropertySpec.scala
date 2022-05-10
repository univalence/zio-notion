package zio.notion.model.page.property

import io.circe.parser.decode

import zio.Scope
import zio.notion.model.common.enumeration.RollupFunction.Count
import zio.notion.model.page.property.Property.{Rollup, _}
import zio.notion.model.page.property.data.RollupData
import zio.test._
import zio.test.Assertion._

object PropertySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Nothing] =
    suite("RollupData serde suite")(
      test("We should be able to parse a rollup object as json") {
        val json: String =
          s"""{
             |    "type": "rollup",
             |    "id": "abc",
             |    "rollup": { 
             |      "type": "number",
             |      "number": 42,
             |      "function": "count" 
             |    }
             |}""".stripMargin

        val expected = Rollup(id = "abc", rollup = RollupData.Number(Some(42d), Count))

        assert(decode[Property](json))(isRight(equalTo(expected)))
      }
    )
}
