package zio.notion.model.database

import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

import zio.notion.model.common.enumeration.{BaseColor, RollupFunction}
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.database.metadata.NumberMetadata.NumberFormat
import zio.notion.model.magnolia.{NoDiscriminantNoNullEncoderDerivation, PropertyTypeEncoderDerivation}

final case class PatchedPropertyDefinition(name: Option[String], propertySchema: Option[PropertySchema])

object PatchedPropertyDefinition {
  val unit: PatchedPropertyDefinition = PatchedPropertyDefinition(None, None)

  sealed trait PropertySchema

  object PropertySchema {
    final case object Title                                  extends PropertySchema
    final case object RichText                               extends PropertySchema
    final case class Number(format: NumberFormat)            extends PropertySchema
    final case class Select(options: Seq[SelectOption])      extends PropertySchema
    final case class MultiSelect(options: Seq[SelectOption]) extends PropertySchema
    final case object Date                                   extends PropertySchema
    final case object People                                 extends PropertySchema
    final case object Files                                  extends PropertySchema
    final case object Checkbox                               extends PropertySchema
    final case object Url                                    extends PropertySchema
    final case object Email                                  extends PropertySchema
    final case object PhoneNumber                            extends PropertySchema
    final case class Formula(expression: String)             extends PropertySchema
    final case class Relation(databaseId: String)            extends PropertySchema

    final case object Status
        extends PropertySchema // TODO: 12/10/22 undocumented yet https://developers.notion.com/reference/update-a-database

    final case class Rollup(
        rollupPropertyName:   Option[String],
        relationPropertyName: Option[String],
        rollupPropertyId:     Option[String],
        relationPropertyId:   Option[String],
        function:             RollupFunction
    ) extends PropertySchema
    final case object CreatedTime    extends PropertySchema
    final case object CreatedBy      extends PropertySchema
    final case object LastEditedTime extends PropertySchema
    final case object LastEditedBy   extends PropertySchema

    final case class SelectOption(name: String, color: Option[BaseColor])

    object SelectOption {
      implicit val encoder: Encoder[SelectOption] = NoDiscriminantNoNullEncoderDerivation.gen[SelectOption]
    }

    implicit val encoder: Encoder[PropertySchema] = PropertyTypeEncoderDerivation.gen[PropertySchema]
  }

  implicit val encoder: Encoder[PatchedPropertyDefinition] =
    (patch: PatchedPropertyDefinition) => {
      val name: Json     = patch.name.fold(Json.obj())(n => Json.obj("name" -> n.asJson))
      val property: Json = patch.propertySchema.fold(Json.obj())(p => p.asJson)

      property.deepMerge(name)
    }
}
