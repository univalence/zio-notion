package zio.notion.model.page

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.{NotionError, Removable}
import zio.notion.Removable.{Ignore, Keep, Remove}
import zio.notion.dsl.PageUpdateDSL._
import zio.notion.model.common.{Cover, Icon, Id, Parent}
import zio.notion.model.magnolia.PatchEncoderDerivation
import zio.notion.model.page.Page.Patch.{Operations, StatelessOperations}
import zio.notion.model.page.Page.Patch.Operations.Operation
import zio.notion.model.page.Page.Patch.Operations.Operation.UpdateProperty.Transform.{Direct, IgnoreEmpty}

import scala.reflect.ClassTag

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Page(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      Id,
    lastEditedBy:   Id,
    id:             String,
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, Property],
    url:            String
)

// val patch =

object Page {

  final case class Patch(
      properties: Map[String, Option[PatchedProperty]],
      archived:   Option[Boolean],
      icon:       Removable[Icon],
      cover:      Removable[Cover]
  ) {
    self =>

    def setOperations(operations: StatelessOperations): Patch =
      operations.operations.foldLeft(self) { case (patch, operation) =>
        operation match {
          case Operation.Archive                     => patch.copy(archived = Some(true))
          case Operation.Unarchive                   => patch.copy(archived = Some(false))
          case Operation.RemoveIcon                  => patch.copy(icon = Remove)
          case Operation.RemoveCover                 => patch.copy(cover = Remove)
          case Operation.SetIcon(icon)               => patch.copy(icon = Keep(icon))
          case Operation.SetCover(cover)             => patch.copy(cover = Keep(cover))
          case Operation.SetProperty(colName, value) => patch.copy(properties = properties + (colName -> Some(value)))
          case Operation.RemoveProperty(key)         => patch.copy(properties = properties + (key -> None))
        }
      }

    def updateOperations(page: Page)(operations: Operations): Either[NotionError, Patch] = {
      val eitherSelf: Either[NotionError, Patch] = Right(self)

      operations.operations.foldLeft(eitherSelf) { case (maybePatch, operation) =>
        maybePatch.flatMap(patch =>
          operation match {
            case stateless: Operation.Stateless => Right(patch.setOperations(stateless))
            case stateful: Operation.Stateful =>
              stateful match {
                case up: Operation.UpdateProperty => up.updateProperty(page, patch)
              }
          }
        )
      }
    }
  }

  object Patch {
    val empty: Patch = Patch(Map.empty, None, Ignore, Ignore)

    final case class StatelessOperations(operations: List[Operation.Stateless]) {
      def ++(operation: Operation.Stateless): StatelessOperations = copy(operations = operations :+ operation)
    }

    final case class Operations(operations: List[Operation]) {
      def ++(operation: Operation): Operations = copy(operations = operations :+ operation)
    }

    object Operations {

      sealed trait Operation

      object Operation {

        sealed trait Stateless extends Operation { self =>
          def ++(operation: Stateless): StatelessOperations = StatelessOperations(List(self, operation))
          def ++(operation: Stateful): Operations           = Operations(List(self, operation))
        }

        sealed trait Stateful extends Operation { self =>
          def ++(operation: Operation): Operations = Operations(List(self, operation))
        }

        case object Archive                                                extends Stateless
        case object Unarchive                                              extends Stateless
        case object RemoveIcon                                             extends Stateless
        case object RemoveCover                                            extends Stateless
        final case class SetIcon(icon: Icon)                               extends Stateless
        final case class SetCover(cover: Cover)                            extends Stateless
        final case class RemoveProperty(name: String)                      extends Stateless
        final case class SetProperty(name: String, value: PatchedProperty) extends Stateless

        final case class UpdateProperty(name: String, transform: UpdateProperty.Transform) extends Stateful {

          // Property Vide => todo à gérer dans le transform
          // Property Not at the right type (PP)
          // 💪💪💪💪😂💩
          def updateProperty(page: Page, patch: Patch): Either[NotionError, Patch] = {
            val patchedProperty: Option[PatchedProperty] =
              patch.properties.getOrElse(name, page.properties.get(name).flatMap(ToPatchedProperty.apply))

            transform.lift(name)(patchedProperty) match {
              case Left(value)  => Left(value)
              case Right(value) => Right(patch.copy(properties = patch.properties + ((name, value)))) // value NO
            }
          }

          // val operation = $"col1".asNumber.patch.ceil.ignoreEmpty
          def ignoreEmpty: UpdateProperty = copy(transform = UpdateProperty.Transform.IgnoreEmpty(transform))
        }

        object UpdateProperty {

          sealed trait Transform { self =>
            def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]]

            def ignoreEmpty: Transform = IgnoreEmpty(self)
          }

          object Transform {

            final case class GenericWithType[PP <: PatchedProperty: ClassTag](transform: Option[PP] => Either[NotionError, Option[PP]])
                extends Transform {

              override def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]] = {
                case Some(pp: PP) => transform(Some(pp))
                case None         => transform(None)
                case Some(x) =>
                  Left(NotionError.PropertyWrongType(name, implicitly[ClassTag[PP]].getClass.getSimpleName, x.getClass.getSimpleName))
              }

            }

            final case class Direct[PP <: PatchedProperty: ClassTag](transform: PP => PP) extends Transform {

              override def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]] = {
                case Some(pp: PP) => Right(Option(transform(pp)))
                case Some(x) =>
                  Left(NotionError.PropertyWrongType(name, implicitly[ClassTag[PP]].getClass.getSimpleName, x.getClass.getSimpleName))
                case None => Left(NotionError.PropertyIsEmpty(name))
              }
            }

            final case class IgnoreEmpty(transform: Transform) extends Transform {

              override def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]] = {
                case None => Right(None)
                case x    => transform.lift(name)(x)
              }
            }
          }

          def succeed[PP <: PatchedProperty: ClassTag](name: String, transform: PP => PP): UpdateProperty =
            UpdateProperty(name, Direct(transform))
        }
      }
    }

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
