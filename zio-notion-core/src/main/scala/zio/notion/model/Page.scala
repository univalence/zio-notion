package zio.notion.model

import io.circe.Encoder
import io.circe.generic.extras._

import zio.{IO, ZIO}
import zio.notion.{NotionError, PropertyNotExist, PropertyWrongType}

import scala.reflect.ClassTag

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Page(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      UserId,
    lastEditedBy:   UserId,
    id:             String,
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, Property],
    url:            String
) { self =>
  def updateProperty[T <: Property: ClassTag](name: String)(update: T => IO[NotionError, T]): IO[NotionError, Page.Patch] =
    Page.Patch(self).updateProperty(name)(update)
}

object Page {
  implicit class IOPatchOps(io: IO[NotionError, Page.Patch]) {
    def updateProperty[T <: Property: ClassTag](key: String)(update: T => IO[NotionError, T]): IO[NotionError, Page.Patch] =
      io.flatMap(_.updateProperty(key)(update))
  }

  final case class Patch(
      page:       Page,
      properties: Map[String, Property],
      archived:   Option[Boolean],
      icon:       Option[Icon],
      cover:      Option[Cover]
  ) { self =>
    def updateProperty[T <: Property: ClassTag](key: String)(update: T => IO[NotionError, T]): IO[NotionError, Page.Patch] =
      page.properties.get(key) match {
        case Some(value) =>
          value match {
            case value: T => update(value).map(v => copy(properties = properties + (key -> v)))
            case _        => ZIO.fail(PropertyWrongType(key, implicitly[ClassTag[T]].runtimeClass.getSimpleName))
          }
        case None => ZIO.fail(PropertyNotExist(key, page.id))
      }
  }

  object Patch {
    def apply(page: Page): Patch = Patch(page, Map.empty, None, None, None)

    implicit val encoder: Encoder[Patch] =
      Encoder.forProduct4("properties", "archived", "icon", "cover")(p => (if (p.properties.isEmpty) None else Some(p.properties), p.archived, p.icon, p.cover))
  }
}
