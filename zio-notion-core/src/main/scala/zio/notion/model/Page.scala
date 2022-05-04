package zio.notion.model

import io.circe.{Encoder, Json}
import io.circe.generic.extras._

import zio.ZIO
import zio.notion.{NotionError, PropertyNotExist, PropertyUpdater, PropertyWrongType, Removable}
import zio.notion.Removable.{Ignore, Keep, Remove}

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
  def patch: Page.Patch = Page.Patch(self)
}

object Page {
  final case class Patch(
      page:       Page,
      properties: Map[String, Property],
      archived:   Option[Boolean],
      icon:       Removable[Icon],
      cover:      Removable[Cover]
  ) { self =>
    def updateProperty[R, E >: NotionError, P <: Property: ClassTag](updater: PropertyUpdater[R, E, P]): ZIO[R, E, Patch] = {
      def updateAllPredicate(f: P => ZIO[R, E, P], predicate: String => Boolean): ZIO[R, E, Patch] = {
        val updatedProperties: Iterable[ZIO[R, E, (String, P)]] =
          page.properties.collect {
            case (key, property: P) if predicate(key) =>
              f(property).map(p => (key, p))
          }

        ZIO.collectAllPar(updatedProperties).map { newProperties =>
          copy(properties = properties ++ newProperties.toMap)
        }
      }

      updater match {
        case PropertyUpdater.OneFieldUpdater(fieldName, f) =>
          page.properties.get(fieldName) match {
            case Some(value) =>
              value match {
                case value: P => f(value).map(v => copy(properties = properties + (fieldName -> v)))
                case _        => ZIO.fail(PropertyWrongType(fieldName, implicitly[ClassTag[P]].runtimeClass.getSimpleName))
              }
            case None => ZIO.fail(PropertyNotExist(fieldName, page.id))
          }
        case PropertyUpdater.AllFieldsUpdater(f)                     => updateAllPredicate(f, _ => true)
        case PropertyUpdater.AllFieldsPredicateUpdater(predicate, f) => updateAllPredicate(f, predicate)
      }
    }

    def archive: Patch = copy(archived = Some(true))

    def unarchive: Patch = copy(archived = Some(false))

    def updateIcon(icon: Icon): Patch = copy(icon = Keep(icon))

    def removeIcon: Patch = copy(icon = Remove)

    def updateCover(cover: Cover): Patch = copy(cover = Keep(cover))

    def removeCover: Patch = copy(cover = Remove)
  }

  object Patch {
    def apply(page: Page): Patch = Patch(page, Map.empty, None, Ignore, Ignore)

    /**
     * The issue here is that the generated Json contains both optional
     * and removable values. It means that a key/value pair should:
     *   - appear if it is a patch
     *   - be null if it is a remove
     *   - disappear if it is an ignore
     *
     * By default we can ask Circe to either remove all nulls or to keep
     * them but here we need both.
     */
    implicit val encoder: Encoder[Patch] =
      (patch: Patch) => {
        def removalEncoding[T: Encoder](key: String, value: Removable[T]): List[(String, Json)] =
          value match {
            case Ignore => List.empty
            case v      => List((key, Encoder[Removable[T]].apply(v)))
          }

        val properties = if (patch.properties.isEmpty) List.empty else List(("properties", Encoder[Map[String, Property]].apply(patch.properties)))
        val archived   = patch.archived.fold(List.empty[(String, Json)])(bool => List(("archived", Encoder[Boolean].apply(bool))))
        val icon       = removalEncoding("icon", patch.icon)
        val cover      = removalEncoding("cover", patch.cover)

        Json.obj(properties ++ archived ++ icon ++ cover: _*)
      }
  }
}
