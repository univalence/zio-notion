package zio.notion.model.page.patch

import io.circe.{Encoder, Json}

import zio.Clock
import zio.notion.PropertyUpdater.{Setter, Transformation, UTransformation}
import zio.notion.model.magnolia.PatchEncoderDerivation
import zio.notion.model.page.property.Link

import java.time.LocalDate

sealed trait PatchedProperty

// TODO: Add set + update + remove for all
// TODO: Add people, formula and rollup patches
object PatchedProperty {
  final case class PatchedNumber(number: Double) extends PatchedProperty

  object PatchedNumber {
    def set(number: Double): Setter[PatchedNumber] = Setter(PatchedNumber(number))

    def update(f: Double => Double): UTransformation[PatchedNumber] =
      Transformation.succeed(number => number.copy(number = f(number.number)))

    def maybeUpdate[E](f: Double => Either[E, Double]): Transformation[E, PatchedNumber] =
      Transformation.apply(number => f(number.number).map(double => PatchedNumber(double)))

    def add(number: Double): UTransformation[PatchedNumber] = update(_ + number)

    def minus(number: Double): UTransformation[PatchedNumber] = update(_ - number)

    def times(number: Double): UTransformation[PatchedNumber] = update(_ * number)

    // Transform to maybe update
    def divide(number: Double): UTransformation[PatchedNumber] = update(_ / number)

    def pow(number: Double): UTransformation[PatchedNumber] = update(Math.pow(_, number))

    def floor: UTransformation[PatchedNumber] = update(Math.floor)

    def ceil: UTransformation[PatchedNumber] = update(Math.ceil)

    implicit val encoder: Encoder[PatchedNumber] = PatchEncoderDerivation.gen[PatchedNumber]
  }

  final case class PatchedUrl(url: String) extends PatchedProperty

  object PatchedUrl {
    def set(url: String): Setter[PatchedUrl] = Setter(PatchedUrl(url))

    implicit val encoder: Encoder[PatchedUrl] = PatchEncoderDerivation.gen[PatchedUrl]
  }

  final case class PatchedSelect(id: Option[String], name: Option[String]) extends PatchedProperty

  object PatchedSelect {
    def set(id: Option[String], name: Option[String]): Setter[PatchedSelect] = Setter(PatchedSelect(id, name))

    def setUsingId(id: String): Setter[PatchedSelect] = set(Some(id), None)

    def setUsingName(name: String): Setter[PatchedSelect] = set(None, Some(name))

    implicit val encoder: Encoder[PatchedSelect] = PatchEncoderDerivation.gen[PatchedSelect]
  }

  final case class PatchedMultiSelect(multiSelect: List[PatchedSelect]) extends PatchedProperty

  object PatchedMultiSelect {
    def set(selects: List[PatchedSelect]): Setter[PatchedMultiSelect] = Setter(PatchedMultiSelect(selects))

    def update(f: List[PatchedSelect] => List[PatchedSelect]): UTransformation[PatchedMultiSelect] =
      Transformation.succeed(multiSelect => multiSelect.copy(multiSelect = f(multiSelect.multiSelect)))

    def removeUsingIdIfExists(id: String): UTransformation[PatchedMultiSelect] = update(_.filterNot(_.id.contains(id)))

    def removeUsingNameIfExists(name: String): UTransformation[PatchedMultiSelect] = update(_.filterNot(_.name.contains(name)))

    def addUsingId(id: String): UTransformation[PatchedMultiSelect] = update(_ :+ PatchedSelect(Some(id), None))

    def addUsingName(name: String): UTransformation[PatchedMultiSelect] = update(_ :+ PatchedSelect(None, Some(name)))

    implicit val encoder: Encoder[PatchedMultiSelect] =
      (property: PatchedMultiSelect) =>
        Json.obj(
          "multi_select" -> Json.arr(
            property.multiSelect.map { select =>
              val name = select.name.fold(List.empty[(String, Json)])(v => List("name" -> Json.fromString(v)))
              val id   = select.id.fold(List.empty[(String, Json)])(v => List("id" -> Json.fromString(v)))
              Json.obj(name ++ id: _*)
            }: _*
          )
        )
  }

  final case class PatchedDate(start: LocalDate, end: Option[LocalDate], timeZone: Option[String]) extends PatchedProperty

  object PatchedDate {
    def set(start: LocalDate, end: Option[LocalDate], timeZone: Option[String]): Setter[PatchedDate] =
      Setter(PatchedDate(start, end, timeZone))

    def startAt(date: LocalDate): Setter[PatchedDate] = set(date, None, None)

    def endAt(f: LocalDate => LocalDate): UTransformation[PatchedDate] =
      Transformation.succeed(property => property.copy(end = Some(f(property.start))))

    def endAt(date: LocalDate): UTransformation[PatchedDate] = endAt(_ => date)

    def between(start: LocalDate, end: LocalDate): Setter[PatchedDate] = set(start, Some(end), None)

    def today: zio.UIO[Setter[PatchedDate]] = Clock.localDateTime.map(_.toLocalDate).map(startAt)

    implicit val encoder: Encoder[PatchedDate] = PatchEncoderDerivation.gen[PatchedDate]
  }

  final case class PatchedEmail(email: String) extends PatchedProperty

  object PatchedEmail {
    def set(email: String): Setter[PatchedEmail] = Setter(PatchedEmail(email))

    def update(f: String => String): UTransformation[PatchedEmail] =
      Transformation.succeed(property => property.copy(email = f(property.email)))

    implicit val encoder: Encoder[PatchedEmail] = PatchEncoderDerivation.gen[PatchedEmail]
  }

  final case class PatchedPhoneNumber(phoneNumber: String) extends PatchedProperty

  object PatchedPhoneNumber {
    def set(phoneNumber: String): Setter[PatchedPhoneNumber] = Setter(PatchedPhoneNumber(phoneNumber))

    def update(f: String => String): UTransformation[PatchedPhoneNumber] =
      Transformation.succeed(property => property.copy(phoneNumber = f(property.phoneNumber)))

    implicit val encoder: Encoder[PatchedPhoneNumber] = PatchEncoderDerivation.gen[PatchedPhoneNumber]
  }

  final case class PatchedCheckbox(checkbox: Boolean) extends PatchedProperty

  object PatchedCheckbox {
    def set(checkbox: Boolean): Setter[PatchedCheckbox] = Setter(PatchedCheckbox(checkbox))

    def update(f: Boolean => Boolean): UTransformation[PatchedCheckbox] =
      Transformation.succeed(property => property.copy(checkbox = f(property.checkbox)))

    def check: Setter[PatchedCheckbox] = set(true)

    def uncheck: Setter[PatchedCheckbox] = set(false)

    def reverse: UTransformation[PatchedCheckbox] = update(!_)

    implicit val encoder: Encoder[PatchedCheckbox] = PatchEncoderDerivation.gen[PatchedCheckbox]
  }

  final case class PatchedFiles(files: Seq[Link]) extends PatchedProperty

  object PatchedFiles {
    def set(files: Seq[Link]): Setter[PatchedFiles] = Setter(PatchedFiles(files))

    def update(f: Seq[Link] => Seq[Link]): UTransformation[PatchedFiles] =
      Transformation.succeed(files => files.copy(files = f(files.files)))

    def add(files: Seq[Link]): UTransformation[PatchedFiles] = update(_ ++ files)

    def add(file: Link): UTransformation[PatchedFiles] = add(List(file))

    def filter(predicate: Link => Boolean): UTransformation[PatchedFiles] = update(_.filter(predicate))

    implicit val encoder: Encoder[PatchedFiles] = PatchEncoderDerivation.gen[PatchedFiles]
  }

  final case class PatchedTitle(title: String) extends PatchedProperty

  object PatchedTitle {
    def set(title: String): Setter[PatchedTitle] = Setter(PatchedTitle(title))

    def update(f: String => String): UTransformation[PatchedTitle] =
      Transformation.succeed(property => property.copy(title = f(property.title)))

    def capitalize: UTransformation[PatchedTitle] = update(_.capitalize)

    implicit val encoder: Encoder[PatchedTitle] =
      (property: PatchedTitle) =>
        Json.obj(
          "title" ->
            Json.arr(
              Json.obj(
                "type" -> Json.fromString("text"),
                "text" -> Json.obj(
                  "content" -> Json.fromString(property.title)
                )
              )
            )
        )
  }

  implicit val encoder: Encoder[PatchedProperty] = PatchEncoderDerivation.gen[PatchedProperty]
}
