package zio.notion

import zio.Scope
import zio.notion.Converter.convertEnumeration
import zio.notion.Faker.{emptyPage, fakeDate, fakeDatetime, fakeUrl, fakeUUID}
import zio.notion.NotionError.ParsingError
import zio.notion.NotionError.PropertyConverterError.{EnumerationError, NestedError, NotExistError, RequiredError}
import zio.notion.model.common.{Id, Period, TimePeriod}
import zio.notion.model.common.enumeration.Color.Default
import zio.notion.model.page.Property
import zio.notion.model.page.property.data.SelectData
import zio.notion.model.user.User.Hidden
import zio.prelude.Validation
import zio.test._

import java.time.{LocalDate, OffsetDateTime}

object ConverterSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Converter suite")(
      test("It should convert a simple page") {
        val number = 10

        final case class CaseClass(test: Double)

        val page = emptyPage.copy(properties = Map("test" -> Property.Number("", Some(number))))

        assertTrue(page.propertiesAs[CaseClass] == Validation.succeed(CaseClass(number)))
      },
      test("It should convert a complex page") {
        val numberValue     = 10
        val booleanValue    = true
        val periodValue     = Period(fakeDate, Some(fakeDate.plusDays(10)))
        val timePeriodValue = TimePeriod(fakeDatetime, Some(fakeDatetime.plusDays(10)), None)

        val properties =
          Map(
            "number"         -> Property.Number("", Some(numberValue)),
            "localDate"      -> Property.Date("", Some(Period(fakeDate, None))),
            "offsetDateTime" -> Property.DateTime("", Some(TimePeriod(fakeDatetime, None, None))),
            "period"         -> Property.Date("", Some(periodValue)),
            "timePeriod"     -> Property.DateTime("", Some(timePeriodValue)),
            "boolean"        -> Property.Checkbox("", Some(booleanValue)),
            "id"             -> Property.People("", Seq(Hidden(fakeUUID))),
            "ids"            -> Property.People("", Seq(Hidden(fakeUUID))),
            "url"            -> Property.Url("", Some(fakeUrl))
          )

        final case class CaseClass(
            number:         Double,
            localDate:      LocalDate,
            offsetDateTime: OffsetDateTime,
            period:         Period,
            timePeriod:     TimePeriod,
            boolean:        Boolean,
            id:             Id,
            ids:            Seq[Id],
            url:            String
        )

        val page = emptyPage.copy(properties = properties)

        val expected =
          CaseClass(
            numberValue,
            fakeDate,
            fakeDatetime,
            periodValue,
            timePeriodValue,
            booleanValue,
            Id(fakeUUID),
            Seq(Id(fakeUUID)),
            fakeUrl
          )

        assertTrue(page.propertiesAs[CaseClass] == Validation.succeed(expected))
      },
      test("It should convert a page with optional value") {
        final case class CaseClass(test: Option[Double])

        val page = emptyPage.copy(properties = Map("test" -> Property.Number("", None)))

        assertTrue(page.propertiesAs[CaseClass] == Validation.succeed(CaseClass(None)))
      },
      test("It should be compatible with enumerations") {
        sealed trait Planet
        object Planet {
          final case object Earth extends Planet
        }

        final case class CaseClass(planet: Planet)

        implicit val planetConverter: PropertyConverter[Planet] = convertEnumeration { case "earth" => Planet.Earth }

        val page = emptyPage.copy(properties = Map("planet" -> Property.Select("", Some(SelectData("", "earth", Default)))))

        assertTrue(page.propertiesAs[CaseClass] == Validation.succeed(CaseClass(Planet.Earth)))
      },
      test("It should convert a page with another notion name") {
        final case class CaseClass(@NotionColumn("Test") test: Option[Double])

        val page = emptyPage.copy(properties = Map("Test" -> Property.Number("", None)))

        assertTrue(page.propertiesAs[CaseClass] == Validation.succeed(CaseClass(None)))
      },
      test("It should not convert a page with missing column") {
        final case class CaseClass(test: Option[Double])

        val page = emptyPage.copy(properties = Map("unknown" -> Property.Number("", None)))

        assertTrue(page.propertiesAs[CaseClass] == Validation.fail(ParsingError("test", NotExistError)))
      },
      test("It should not convert a page with missing required value") {
        final case class CaseClass(test: Double)

        val page = emptyPage.copy(properties = Map("test" -> Property.Number("", None)))

        assertTrue(page.propertiesAs[CaseClass] == Validation.fail(ParsingError("test", RequiredError)))
      },
      test("It should not convert a page with a nested page") {
        final case class CaseClass(nested: CaseClass)

        val page = emptyPage.copy(properties = Map("nested" -> Property.Number("", None)))

        assertTrue(page.propertiesAs[CaseClass] == Validation.fail(ParsingError("nested", NestedError)))
      },
      test("It should not convert a page with unknown enumeration") {
        sealed trait Planet
        object Planet {
          final case object Earth extends Planet
        }

        final case class CaseClass(planet: Planet)

        implicit val planetConverter: PropertyConverter[Planet] = convertEnumeration { case "earth" => Planet.Earth }

        val page = emptyPage.copy(properties = Map("planet" -> Property.Select("", Some(SelectData("", "mars", Default)))))

        assertTrue(page.propertiesAs[CaseClass] == Validation.fail(ParsingError("planet", EnumerationError("mars", "Planet"))))
      }
    )
}
