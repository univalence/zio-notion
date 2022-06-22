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
          contentType    <- c.downField("type").as[String]
          content <-
            contentType match {
              case "unsupported"        => Right(Unsupported)
              case "paragraph"          => c.downField("paragraph").as[Paragraph]
              case "heading_1"          => c.downField("heading_1").as[HeadingOne]
              case "heading_2"          => c.downField("heading_2").as[HeadingTwo]
              case "heading_3"          => c.downField("heading_3").as[HeadingThree]
              case "callout"            => c.downField("callout").as[Callout]
              case "quote"              => c.downField("quote").as[Quote]
              case "to_do"              => c.downField("to_do").as[ToDo]
              case "bulleted_list_item" => c.downField("bulleted_list_item").as[BulletedListItem]
              case "numbered_list_item" => c.downField("numbered_list_item").as[NumberedListItem]
              case "toggle"             => c.downField("toggle").as[Toggle]
              case "code"               => c.downField("code").as[Code]
              case "child_page"         => c.downField("child_page").as[ChildPage]
              case "child_database"     => c.downField("child_database").as[ChildDatabase]
              case "embed"              => c.downField("embed").as[Embed]
              case "image"              => c.downField("image").as[Image]
              case "video"              => c.downField("video").as[Video]
              case "file"               => c.downField("file").as[File]
              case "pdf"                => c.downField("pdf").as[Pdf]
              case "bookmark"           => c.downField("bookmark").as[Bookmark]
              case "equation"           => c.downField("equation").as[Equation]
              case "divider"            => Right(Divider)
              case "table_of_contents"  => c.downField("table_of_contents").as[TableOfContents]
              case "breadcrumb"         => Right(Breadcrumb)
              case "column_list"        => c.downField("column_list").as[ColumnList]
              case "column"             => c.downField("column").as[Column]
              case contentType          => Left(DecodingFailure(s"The type '$contentType' is unknown for block", c.history))
            }
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
