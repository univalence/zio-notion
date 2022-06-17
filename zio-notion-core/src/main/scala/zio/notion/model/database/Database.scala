package zio.notion.model.database

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.NotionError
import zio.notion.NotionError.PropertyNotExist
import zio.notion.model.common.{Cover, Icon, Id, Parent}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.database.Database.Patch.{Operations, StatelessOperations}
import zio.notion.model.database.Database.Patch.Operations.Operation
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.magnolia.PatchEncoderDerivation

import java.time.OffsetDateTime

@ConfiguredJsonCodec(decodeOnly = true)
final case class Database(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      Id,
    lastEditedBy:   Id,
    id:             String,
    title:          Seq[RichTextFragment],
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, PropertyDefinition],
    url:            String
)

object Database {

  final case class Patch(
      title:      Option[Seq[RichTextFragment]],
      properties: Map[String, Option[PatchedPropertyDefinition]]
  ) { self =>

    def setOperation(operation: Operation.Stateless): Patch =
      operation match {
        case Operation.RemoveColumn(name) => copy(properties = properties + (name -> None))
        case Operation.SetTitle(title)    => copy(title = Some(title))
        case Operation.CreateColumn(name, schema) =>
          val propertyDefinition = PatchedPropertyDefinition(None, Some(schema))
          copy(properties = properties + (name -> Some(propertyDefinition)))
      }

    def setOperations(operations: StatelessOperations): Patch =
      operations.operations.foldLeft(self)((patch, operation) => patch.setOperation(operation))

    def updateOperation(database: Database, operation: Operation): Either[NotionError, Patch] =
      operation match {
        case stateless: Operation.Stateless => Right(setOperations(StatelessOperations(List(stateless))))
        case stateful: Operation.Stateful =>
          stateful match {
            case Operation.UpdateTitle(f) =>
              val oldTitle: Seq[RichTextFragment] = title.getOrElse(database.title)
              Right(copy(title = Some(f(oldTitle))))
            case Operation.UpdateColumn(name, update) =>
              database.properties.get(name) match {
                case Some(_) => Right(copy(properties = properties + (name -> Some(update))))
                case None    => Left(PropertyNotExist(name, database.id))
              }
          }
      }

    def updateOperations(database: Database, operations: Operations): Either[NotionError, Patch] = {
      val eitherSelf: Either[NotionError, Patch] = Right(self)

      operations.operations.foldLeft(eitherSelf) { (maybePatch, operation) =>
        maybePatch.flatMap(patch => patch.updateOperation(database, operation))
      }
    }
  }

  object Patch {
    val empty: Patch = Patch(None, Map.empty)

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]

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
        final case class RemoveColumn(name: String)                         extends Stateless
        final case class SetTitle(title: Seq[RichTextFragment])             extends Stateless
        final case class CreateColumn(name: String, schema: PropertySchema) extends Stateless

        final case class UpdateTitle(f: Seq[RichTextFragment] => Seq[RichTextFragment]) extends Stateful

        final case class UpdateColumn(name: String, update: PatchedPropertyDefinition) extends Stateful {
          def rename(name: String): UpdateColumn = copy(update = update.copy(name = Some(name)))

          def as(propertySchema: PropertySchema): UpdateColumn = copy(update = update.copy(propertySchema = Some(propertySchema)))
        }
      }
    }
  }
}
