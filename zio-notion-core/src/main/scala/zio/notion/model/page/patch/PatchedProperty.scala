package zio.notion.model.page.patch

import io.circe.Encoder

import zio.Clock
import zio.notion.PropertyUpdater.{Setter, UTransformation}
import zio.notion.model.magnolia.PatchEncoderDerivation

import java.time.LocalDate

sealed trait PatchedProperty

object PatchedProperty {
  case class PatchedNumber(number: Double) extends PatchedProperty

  object PatchedNumber {
    def set(number: Double): Setter[PatchedNumber] = () => PatchedNumber(number)

    def op(f: Double => Double): UTransformation[PatchedNumber] = number => Right(number.copy(number = f(number.number)))

    def add(number: Double): UTransformation[PatchedNumber] = op(_ + number)

    def minus(number: Double): UTransformation[PatchedNumber] = op(_ - number)

    def times(number: Double): UTransformation[PatchedNumber] = op(_ * number)

    def divide(number: Double): UTransformation[PatchedNumber] = op(_ / number)

    def floor: UTransformation[PatchedNumber] = op(Math.floor)

    def ceil: UTransformation[PatchedNumber] = op(Math.ceil)
  }

  case class PatchedUrl(url: String) extends PatchedProperty

  object PatchedUrl {
    def set(url: String): Setter[PatchedUrl] = Setter(PatchedUrl(url))
  }

  case class PatchedSelect(id: Option[String], name: Option[String]) extends PatchedProperty

  object PatchedSelect {
    def setUsingId(id: String): Setter[PatchedSelect] = Setter(PatchedSelect(Some(id), None))

    def setUsingName(name: String): Setter[PatchedSelect] = Setter(PatchedSelect(None, Some(name)))
  }

  case class PatchedDate(startDate: LocalDate) extends PatchedProperty

  object PatchedDate {
    def today: zio.UIO[Setter[PatchedDate]] = Clock.localDateTime.map(_.toLocalDate).map(date => Setter(PatchedDate(date)))
  }

  implicit val encoder: Encoder[PatchedProperty] = PatchEncoderDerivation.gen[PatchedProperty]
}
