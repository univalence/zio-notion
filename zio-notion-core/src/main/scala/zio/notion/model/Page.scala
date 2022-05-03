package zio.notion.model

import io.circe.Encoder
import io.circe.generic.extras._

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
  def updateProperty[T <: Property](name: String)(update: T => T): Page.Patch = Page.Patch(self).updateProperty(name)(update)
}

object Page {
  final case class Patch(
      page:       Page,
      properties: Map[String, Property],
      archived:   Option[Boolean],
      icon:       Option[Icon],
      cover:      Option[Cover]
  ) { self =>
    def updateProperty[T <: Property](key: String)(update: T => T): Page.Patch =
      page.properties.get(key) match {
        case Some(value) =>
          value match {
            case value: T => copy(properties = properties + (key -> update(value)))
            case _        => self // TODO: should return error
          }
        case None => self // TODO: should return error
      }
  }

  object Patch {
    def apply(page: Page): Patch = Patch(page, Map.empty, None, None, None)

    implicit val encoder: Encoder[Patch] =
      Encoder.forProduct4("properties", "archived", "icon", "cover")(p => (if (p.properties.isEmpty) None else Some(p.properties), p.archived, p.icon, p.cover))
  }
}
