package zio.notion.model.block

import io.circe._
import io.circe.Decoder.Result
import io.circe.syntax.EncoderOps

import zio.notion.model.block.BlockContent._
import zio.notion.model.common.Id

import java.time.OffsetDateTime

final case class Block(
    id:             String,
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      Id,
    lastEditedBy:   Id,
    archived:       Boolean,
    hasChildren:    Boolean,
    content:        BlockContent
)

object Block {

  implicit val codec: Codec[Block] =
    new Codec[Block] {

      override def apply(c: HCursor): Result[Block] =
        for {
          id             <- c.downField("id").as[String]
          createdTime    <- c.downField("created_time").as[OffsetDateTime]
          lastEditedTime <- c.downField("last_edited_time").as[OffsetDateTime]
          createdBy      <- c.downField("created_by").as[Id]
          lastEditedBy   <- c.downField("last_edited_by").as[Id]
          hasChildren    <- c.downField("has_children").as[Boolean]
          archived       <- c.downField("archived").as[Boolean]
          content        <- c.as[BlockContent]
        } yield Block(
          id             = id,
          createdTime    = createdTime,
          lastEditedTime = lastEditedTime,
          createdBy      = createdBy,
          lastEditedBy   = lastEditedBy,
          archived       = archived,
          hasChildren    = hasChildren,
          content        = content
        )

      override def apply(block: Block): Json = {
        val metadata =
          Json
            .obj(
              "id"               -> block.id.asJson,
              "created_time"     -> block.createdTime.asJson,
              "last_edited_time" -> block.lastEditedTime.asJson,
              "created_by"       -> block.createdBy.asJson,
              "last_edited_by"   -> block.lastEditedBy.asJson,
              "archived"         -> block.archived.asJson,
              "has_children"     -> block.hasChildren.asJson
            )

        Encoder[BlockContent].apply(block.content) deepMerge metadata
      }

    }
}
