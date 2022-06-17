package zio.notion

import io.circe.jawn
import magnolia1.{CaseClass, Magnolia}

import zio.notion.Converter.parseEnumeration
import zio.notion.NotionError.{ParsingError, PropertyConverterError}
import zio.notion.NotionError.PropertyConverterError._
import zio.notion.Test.Status.{Posted, Scala}
import zio.notion.model.common.{Id, Period, TimePeriod}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.page.{Page, Property}
import zio.notion.model.page.Property.{People, Relation, RichText, Title}
import zio.prelude.{Validation, ZValidation}

import scala.reflect.ClassTag

import java.time.{LocalDate, OffsetDateTime}

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

  def parseEnumeration[A](convert: PartialFunction[String, A])(implicit tag: ClassTag[A]): PropertyConverter[A] =
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
    case Property.MultiSelect(_, multiSelect) =>
      Validation.validateAll(multiSelect.map(select => Property.Select("", Some(select))).map(A.convert))
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

object Test extends App {
  final case class Foo(@NotionColumn("Mots clés") status: List[Status], @NotionColumn("Date de publication") date: LocalDate)

  sealed trait Status

  object Status {
    case object Posted extends Status
    case object Scala  extends Status

  }

  implicit val status: PropertyConverter[Status] =
    parseEnumeration {
      case "Poste"  => Posted
      case "scala2" => Scala
    }

  def pagePayload(pageId: String): String =
    s"""{
       |    "object": "page",
       |    "id": "$pageId",
       |    "created_time": "2022-01-28T10:47:00.000Z",
       |    "last_edited_time": "2022-02-22T08:30:00.000Z",
       |    "created_by": {
       |        "object": "user",
       |        "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71"
       |    },
       |    "last_edited_by": {
       |        "object": "user",
       |        "id": "e20722e7-0439-4122-aa99-8304e561bc1d"
       |    },
       |    "cover": null,
       |    "icon": null,
       |    "parent": {
       |        "type": "database_id",
       |        "database_id": "3868f708-ae46-461f-bfcf-72d34c9536f9"
       |    },
       |    "archived": false,
       |    "properties": {
       |        "Number": {
       |            "id": ">QlB",
       |            "type": "number",
       |            "number": 10
       |        },
       |        "value": {
       |            "id": ">QlB",
       |            "type": "number",
       |            "number": 20
       |        },
       |        "Status": {
       |            "id": "@Gcb",
       |            "type": "select",
       |            "select": {
       |                "id": "150fbaa5-7b63-4903-855c-c6973cf0ac48",
       |                "name": "Posted",
       |                "color": "green"
       |            }
       |        },
       |        "Date de publication": {
       |            "id": "DY:@",
       |            "type": "date",
       |            "date": {
       |                "start": "2022-02-22",
       |                "end": null,
       |                "time_zone": null
       |            }
       |        },
       |        "Reviewed by": {
       |            "id": "T[X?",
       |            "type": "people",
       |            "people": []
       |        },
       |        "Mots clés": {
       |            "id": "Xz}B",
       |            "type": "multi_select",
       |            "multi_select": [
       |                {
       |                    "id": "ee7d5ee6-6df1-455d-b0c3-4aee64a0bc63",
       |                    "name": "scala2",
       |                    "color": "brown"
       |                },
       |                {
       |                    "id": "ee7d5ee6-6df1-455d-b0c3-4aee64a0bc63",
       |                    "name": "Poste",
       |                    "color": "brown"
       |                }
       |            ]
       |        },
       |        "Link": {
       |            "id": "jJz@",
       |            "type": "rich_text",
       |            "rich_text": [
       |                {
       |                    "type": "text",
       |                    "text": {
       |                        "content": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
       |                        "link": {
       |                            "url": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
       |                        }
       |                    },
       |                    "annotations": {
       |                        "bold": false,
       |                        "italic": false,
       |                        "strikethrough": false,
       |                        "underline": false,
       |                        "code": false,
       |                        "color": "default"
       |                    },
       |                    "plain_text": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
       |                    "href": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
       |                }
       |            ]
       |        },
       |        "Auteur": {
       |            "id": "pJG_",
       |            "type": "people",
       |            "people": [
       |                {
       |                    "object": "user",
       |                    "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71",
       |                    "name": "Dylan DO AMARAL",
       |                    "avatar_url": "https://lh3.googleusercontent.com/-6B9dSBYVuvk/AAAAAAAAAAI/AAAAAAAAAAc/su4if5KvrbA/photo.jpg",
       |                    "type": "person",
       |                    "person": {}
       |                }
       |            ]
       |        },
       |        "Type": {
       |            "id": "|=~Q",
       |            "type": "select",
       |            "select": {
       |                "id": "01f5896b-8c7c-4c6c-a76f-c1c2eb6d12c9",
       |                "name": "Tips",
       |                "color": "purple"
       |            }
       |        },
       |        "Name": {
       |            "id": "title",
       |            "type": "title",
       |            "title": [
       |                {
       |                    "type": "text",
       |                    "text": {
       |                        "content": "Les énumérations en Scala 2.X",
       |                        "link": null
       |                    },
       |                    "annotations": {
       |                        "bold": false,
       |                        "italic": false,
       |                        "strikethrough": false,
       |                        "underline": false,
       |                        "code": false,
       |                        "color": "default"
       |                    },
       |                    "plain_text": "Les énumérations en Scala 2.X",
       |                    "href": null
       |                }
       |            ]
       |        }
       |    },
       |    "url": "https://www.notion.so/Les-num-rations-en-Scala-2-X-1c2d0a80332146419615f345185de05a"
       |}""".stripMargin

  jawn.decode[Page](pagePayload("test")).foreach { page =>
    val instance: Converter[Foo] = Converter.gen[Foo]

    instance match {
      case converter: PageConverter[_] =>
        val foo = converter.convert(page.properties)
        val message =
          foo match {
            case ZValidation.Failure(_, errors) => errors.map(_.humanize).mkString("\n")
            case ZValidation.Success(_, value)  => value
          }
        println(message)
      case _ => println("Nop")
    }
  }

}
