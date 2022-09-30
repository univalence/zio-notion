package zio.notion

import magnolia1.{CaseClass, Magnolia}
import zio.notion.NotionError.PropertyConverterError._
import zio.notion.NotionError.{ParsingError, PropertyConverterError}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.common.{Id, Period, TimePeriod}
import zio.notion.model.page.Property
import zio.notion.model.page.Property.{Files, People, Relation, RichText, Select, Title}
import zio.prelude.{Validation, ZValidation}

import java.time.{LocalDate, OffsetDateTime}
import scala.reflect.ClassTag

final class NotionColumn(val name: String) extends scala.annotation.StaticAnnotation

sealed trait Converter[A]

trait PropertyConverter[A] extends Converter[A] { self => // scalafix:ok
  def convert(p: Property): Validation[PropertyConverterError, A]

  def map[B](f: A => B): PropertyConverter[B] = (p: Property) => self.convert(p).map(f)

  def flatMap[B](f: A => Validation[PropertyConverterError, B]): PropertyConverter[B] = (p: Property) => self.convert(p).flatMap(f)
}

trait PageConverter[A] extends Converter[A] { // scalafix:ok
  def convert(properties: Map[String, Property]): Validation[ParsingError, A]
}

object Converter {
  type Typeclass[T] = Converter[T]

  def required[A](option: Option[A]): Validation[PropertyConverterError, A] = Validation.fromOptionWith(RequiredError)(option)

  def parseNumber[A](f: Double => Option[A])(stype: String): PropertyConverter[A] = {
    case Property.Number(_, number) => required(number).flatMap(n => Validation.fromOptionWith(NotParsableError(stype))(f(n)))
    case _                          => Validation.fail(NotParsableError(stype))
  }

  def convertEnumeration[A](convert: PartialFunction[String, A])(implicit tag: ClassTag[A]): PropertyConverter[A] =
    (p: Property) => {
      val className: String =
        tag.runtimeClass.getSimpleName match {
          case s"$name$$$_" => name
          case name         => name
        }

      p match {
        case Property.Select(_, select) =>
          required(select)
            .map(_.name)
            .flatMap(name => Validation.fromOptionWith(EnumerationError(name, className))(convert.lift(name)))
        case _ => Validation.fail(NotParsableError(className))
      }
    }

  implicit def list[A](implicit A: PropertyConverter[Seq[A]]): PropertyConverter[List[A]] = (p: Property) => A.convert(p).map(_.toList)

  implicit def seq[A](implicit A: PropertyConverter[A], tag: ClassTag[A]): PropertyConverter[Seq[A]] = {
    case Property.Files(_, files) =>
      Validation.validateAll(files.map(link => Files("", Seq(link))).map(A.convert))
    case Property.MultiSelect(_, multiSelect) =>
      Validation.validateAll(multiSelect.map(select => Select("", Some(select))).map(A.convert))
    case Property.People(_, people) =>
      Validation.validateAll(people.map(person => People("", Seq(person))).map(A.convert))
    case Property.Relation(_, relation) =>
      Validation.validateAll(relation.map(r => Relation("", Seq(r))).map(A.convert))
    case Property.Title(_, title) =>
      Validation.validateAll(title.map(text => Title("", Seq(text))).map(A.convert))
    case Property.RichText(_, richText) =>
      Validation.validateAll(richText.map(text => RichText("", Seq(text))).map(A.convert))
    case _ => Validation.fail(NotParsableError(tag.runtimeClass.getSimpleName))
  }

  implicit val boolean: PropertyConverter[Boolean] = {
    case Property.Checkbox(_, checkbox) => required(checkbox)
    case _                              => Validation.fail(NotParsableError("Boolean"))
  }

  implicit val double: PropertyConverter[Double] = parseNumber(Some.apply)("Double")

  implicit val float: PropertyConverter[Float] = parseNumber(v => Some(v.toFloat))("Long")

  implicit val int: PropertyConverter[Int] = parseNumber(v => if (v.isValidInt) Some(v.toInt) else None)("Int")

  implicit val long: PropertyConverter[Long] =
    parseNumber { v =>
      val isValidFloat = { val l = v.toLong; l.toDouble == v && l != Long.MaxValue }
      if (isValidFloat) Some(v.toLong) else None
    }("Long")



  /**
   * A best effort conversion to look like notion database default value
   * representation.
   */
  implicit val string: PropertyConverter[String] = {
    case Property.Number(_, number)                 => required(number).map(_.toString)
    case Property.Url(_, url)                       => required(url)
    case Property.Select(_, select)                 => required(select).map(_.name)
    case Property.MultiSelect(_, multiSelect)       => Validation.succeed(multiSelect.map(_.name).mkString(", "))
    case Property.Date(_, period)                   => required(period).map(_.toString)
    case Property.DateTime(_, period)               => required(period).map(_.toString)
    case Property.Email(_, email)                   => required(email)
    case Property.PhoneNumber(_, phoneNumber)       => required(phoneNumber)
    case Property.Checkbox(_, checkbox)             => required(checkbox).map(_.toString)
    case Property.Files(_, files)                   => Validation.succeed(files.map(_.toString).mkString(", "))
    case Property.Title(_, title)                   => Validation.succeed(title.map(_.toString).mkString(""))
    case Property.RichText(_, richText)             => Validation.succeed(richText.map(_.toString).mkString(""))
    case Property.People(_, people)                 => Validation.succeed(people.map(_.toString).mkString(", "))
    case Property.Relation(_, relation)             => Validation.succeed(relation.map(_.id).mkString(", "))
    case Property.CreatedBy(_, createdBy)           => Validation.succeed(createdBy).map(_.id)
    case Property.CreatedTime(_, createdTime)       => Validation.succeed(createdTime)
    case Property.LastEditedBy(_, lastEditedBy)     => Validation.succeed(lastEditedBy).map(_.id)
    case Property.LastEditedTime(_, lastEditedTime) => Validation.succeed(lastEditedTime)
    case _                                          => Validation.fail(NotParsableError("String"))
  }

  implicit val localDate: PropertyConverter[LocalDate] = {
    case Property.Date(_, data)     => required(data).map(_.start)
    case Property.DateTime(_, data) => required(data).map(_.start.toLocalDate)
    case _                          => Validation.fail(NotParsableError("LocalDate"))
  }

  implicit val localDateTime: PropertyConverter[OffsetDateTime] = {
    case Property.DateTime(_, data) => required(data).map(_.start)
    case _                          => Validation.fail(NotParsableError("LocalDateTime"))
  }

  implicit val period: PropertyConverter[Period] = {
    case Property.Date(_, data)     => required(data)
    case Property.DateTime(_, data) => required(data).map(_.toPeriod)
    case _                          => Validation.fail(NotParsableError("Period"))
  }

  implicit val timePeriod: PropertyConverter[TimePeriod] = {
    case Property.DateTime(_, data) => required(data)
    case _                          => Validation.fail(NotParsableError("TimePeriod"))
  }

  implicit val id: PropertyConverter[Id] = {
    case Property.People(_, people)             => required(people.headOption).map(person => Id(person.id))
    case Property.Relation(_, relation)         => required(relation.headOption)
    case Property.CreatedBy(_, createdBy)       => Validation.succeed(createdBy)
    case Property.LastEditedBy(_, lastEditedBy) => Validation.succeed(lastEditedBy)
    case _                                      => Validation.fail(NotParsableError("Id"))
  }

  implicit val richTextFragment: PropertyConverter[RichTextFragment] = {
    case Property.Title(_, title)       => required(title.headOption)
    case Property.RichText(_, richText) => required(richText.headOption)
    case _                              => Validation.fail(NotParsableError("RichText"))
  }

  implicit def optional[A](implicit A: PropertyConverter[A]): PropertyConverter[Option[A]] =
    (p: Property) =>
      A.convert(p) match {
        case ZValidation.Failure(log, errors) =>
          errors.toList match {
            case head :: Nil =>
              head match {
                case PropertyConverterError.RequiredError => Validation.succeed(None)
                case error                                => Validation.fail(error)
              }
            case _ => ZValidation.Failure(log, errors)
          }
        case validation => validation.map(Some.apply)
      }

  def join[T](ctx: CaseClass[Converter, T]): Converter[T] =
    new PageConverter[T] {

      override def convert(properties: Map[String, Property]): Validation[ParsingError, T] = {
        val parameters: Seq[Validation[ParsingError, Any]] =
          ctx.parameters.map { parameter =>
            val maybeProperty =
              properties
                .get(
                  parameter.annotations
                    .collectFirst { case field: NotionColumn => field.name }
                    .getOrElse(parameter.label)
                )

            val validations =
              maybeProperty match {
                case Some(property) =>
                  parameter.typeclass match {
                    case converter: PropertyConverter[parameter.PType] => converter.convert(property)
                    case _                                             => Validation.fail(NestedError)
                  }
                case None => Validation.fail(NotExistError)
              }

            validations.mapError(error => ParsingError(parameter.label, error))
          }
        Validation.validateAll(parameters).map(parameters => ctx.rawConstruct(parameters))
      }
    }

  implicit def gen[T]: Converter[T] = macro Magnolia.gen[T]
}
