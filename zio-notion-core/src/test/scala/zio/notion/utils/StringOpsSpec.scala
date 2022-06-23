package zio.notion.utils

import zio.Scope
import zio.notion.utils.StringOps._
import zio.test._

object StringOpsSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("digitify snake case headers")(
      test("should replace one with 1             ")(assertTrue(digitify("heading_one") == "heading_1")),
      test("should replace two with 2             ")(assertTrue(digitify("heading_two") == "heading_2")),
      test("should replace three with 3           ")(assertTrue(digitify("heading_three") == "heading_3")),
      test("should not replace two if not isolated")(assertTrue(digitify("heading_twosome") == "heading_twosome")),
      test("should not replace a non number string")(assertTrue(digitify("heading_honey") == "heading_honey"))
    )
}
