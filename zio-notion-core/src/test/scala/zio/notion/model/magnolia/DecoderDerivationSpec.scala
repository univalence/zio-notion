package zio.notion.model.magnolia

import io.circe.Decoder
import io.circe.jawn.decode

import zio.Scope
import zio.test._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object DecoderDerivationSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("JSON decoder derivations suite")(
      test("should parse json to case class Foo") {
        val json = """{"foo": "bar"}"""
        case class Foo(foo: String)

        implicit val decoder: Decoder[Foo] = DecoderDerivation.gen[Foo]

        assertTrue(decode[Foo](json) == Right(Foo("bar")))
      },
      test("should use default value") {
        val json = """{"foo": "bar"}"""
        case class Foo(foo: String, baz: String = "default")

        implicit val decoder: Decoder[Foo] = DecoderDerivation.gen[Foo]

        assertTrue(decode[Foo](json) == Right(Foo("bar")))
      },
      test("should not use default value") {
        val json = """{"foo": "bar", "baz": "bouse"}"""
        case class Foo(foo: String, baz: String = "default")

        implicit val decoder: Decoder[Foo] = DecoderDerivation.gen[Foo]

        assertTrue(decode[Foo](json) == Right(Foo("bar", "bouse")))
      }
    )

}
