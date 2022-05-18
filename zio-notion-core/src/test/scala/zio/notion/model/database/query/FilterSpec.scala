package zio.notion.model.database.query

import io.circe.syntax._

import zio.Scope
import zio.notion.model.database.query.PropertyFilter._
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.StartsWith
import zio.notion.model.printer
import zio.test._

object FilterSpec extends ZIOSpecDefault {
  val propertyFilter1: Filter = Filter.One(Title("Title", StartsWith("Toto")))
  val propertyFilter2: Filter = Filter.One(Checkbox("Checkbox", CheckboxPropertyFilter.Equals(true)))
  val propertyFilter3: Filter = Filter.One(Number("Number", NumberPropertyFilter.LessThan(10)))
  val propertyFilter4: Filter = Filter.One(Select("Select", IsEmpty(true)))

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Filter suite")(
      test("One filter serialization") {
        val filter: Filter = propertyFilter1

        val expected: String =
          """{
            |  "property" : "Title",
            |  "title" : {
            |    "starts_with" : "Toto"
            |  },
            |  "type" : "title"
            |}""".stripMargin

        assertTrue(printer.print(filter.asJson) == expected)
      },
      test("And filter serialization") {
        val filter: Filter = Filter.And(propertyFilter1, propertyFilter2)

        val expected: String =
          """{
            |  "and" : [
            |    {
            |      "property" : "Title",
            |      "title" : {
            |        "starts_with" : "Toto"
            |      },
            |      "type" : "title"
            |    },
            |    {
            |      "property" : "Checkbox",
            |      "checkbox" : {
            |        "equals" : true
            |      },
            |      "type" : "checkbox"
            |    }
            |  ]
            |}""".stripMargin

        assertTrue(printer.print(filter.asJson) == expected)
      },
      test("Or filter serialization") {
        val filter: Filter = Filter.Or(propertyFilter1, propertyFilter2)

        val expected: String =
          """{
            |  "or" : [
            |    {
            |      "property" : "Title",
            |      "title" : {
            |        "starts_with" : "Toto"
            |      },
            |      "type" : "title"
            |    },
            |    {
            |      "property" : "Checkbox",
            |      "checkbox" : {
            |        "equals" : true
            |      },
            |      "type" : "checkbox"
            |    }
            |  ]
            |}""".stripMargin

        assertTrue(printer.print(filter.asJson) == expected)
      },
      test("Nested filter serialization") {
        val filter: Filter = Filter.And(propertyFilter1, Filter.Or(Filter.And(propertyFilter2, propertyFilter3), propertyFilter4))

        val expected: String =
          """{
            |  "and" : [
            |    {
            |      "property" : "Title",
            |      "title" : {
            |        "starts_with" : "Toto"
            |      },
            |      "type" : "title"
            |    },
            |    {
            |      "or" : [
            |        {
            |          "and" : [
            |            {
            |              "property" : "Checkbox",
            |              "checkbox" : {
            |                "equals" : true
            |              },
            |              "type" : "checkbox"
            |            },
            |            {
            |              "property" : "Number",
            |              "number" : {
            |                "less_than" : 10.0
            |              },
            |              "type" : "number"
            |            }
            |          ]
            |        },
            |        {
            |          "property" : "Select",
            |          "select" : {
            |            "is_empty" : true
            |          },
            |          "type" : "select"
            |        }
            |      ]
            |    }
            |  ]
            |}""".stripMargin

        assertTrue(printer.print(filter.asJson) == expected)
      },
      test("One and One combination") {
        val left: Filter  = propertyFilter1
        val right: Filter = propertyFilter2

        val expected: Filter = Filter.And(propertyFilter1, propertyFilter2)

        assertTrue(left.and(right) == expected)
      },
      test("One and Or combination") {
        val one: Filter = propertyFilter1
        val or: Filter  = Filter.Or(propertyFilter2, propertyFilter3)

        val expected: Filter = Filter.And(propertyFilter1, Filter.Or(propertyFilter2, propertyFilter3))

        assertTrue(one.and(or) == expected)
      },
      test("One and And combination") {
        val one: Filter = propertyFilter1
        val and: Filter = Filter.And(propertyFilter2, propertyFilter3)

        val expected: Filter = Filter.And(propertyFilter1, propertyFilter2, propertyFilter3)

        assertTrue(one.and(and) == expected)
      },
      test("One or One combination") {
        val left: Filter  = propertyFilter1
        val right: Filter = propertyFilter2

        val expected: Filter = Filter.Or(propertyFilter1, propertyFilter2)

        assertTrue(left.or(right) == expected)
      },
      test("One or Or combination") {
        val one: Filter = propertyFilter1
        val or: Filter  = Filter.Or(propertyFilter2, propertyFilter3)

        val expected: Filter = Filter.Or(propertyFilter1, propertyFilter2, propertyFilter3)

        assertTrue(one.or(or) == expected)
      },
      test("One or And combination") {
        val one: Filter = propertyFilter1
        val and: Filter = Filter.And(propertyFilter2, propertyFilter3)

        val expected: Filter = Filter.Or(propertyFilter1, Filter.And(propertyFilter2, propertyFilter3))

        assertTrue(one.or(and) == expected)
      }
    )
}
