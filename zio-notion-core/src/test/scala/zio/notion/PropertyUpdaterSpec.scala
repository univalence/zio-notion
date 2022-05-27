package zio.notion

import zio.Scope
import zio.notion.Faker.fakeLocalDate
import zio.notion.PropertyUpdater.{FieldSetter, FieldUpdater}
import zio.notion.dsl._
import zio.notion.model.page.PatchedProperty.{PatchedDate, PatchedNumber}
import zio.test._

object PropertyUpdaterSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Property Updater suite")(
      test("We can map a setter") {
        val patch: FieldSetter[PatchedDate] =
          allColumns.asDate.patch
            .startAt(fakeLocalDate)
            .map(property => property.copy(start = property.start.plusDays(5)))

        assertTrue(patch.value.start == fakeLocalDate.plusDays(5))
      },
      test("We can map a transformation") {
        val patch: FieldUpdater[Nothing, PatchedNumber] =
          allColumns.asNumber.patch.ceil
            .map(property => property.copy(number = property.number + 10))

        assertTrue(patch.f(PatchedNumber(2.5)).map(_.number) == Right(13d))

      }
    )
}
