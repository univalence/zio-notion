package zio.notion.model.database

import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.model.database.query.Query
import zio.notion.model.printer
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object QuerySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Query suite")(test("Generate empty query") {

      val sort = Query.empty

      val expected: String =
        """{
          |  
          |}""".stripMargin

      assertTrue(printer.print(sort.asJson) == expected)
    })
}
