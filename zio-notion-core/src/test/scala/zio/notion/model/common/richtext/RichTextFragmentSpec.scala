package zio.notion.model.common.richtext

import zio.Scope
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object RichTextFragmentSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("RichTextFragment toString spec") {
      test("Text to string") {
        val text = RichTextFragment.default("Test", Annotations.default)

        assertTrue(text.toString == "Test")
      }
    }
}
