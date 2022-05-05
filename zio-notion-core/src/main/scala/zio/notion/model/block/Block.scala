package zio.notion.model.block

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.UserId
import zio.notion.model.common.richtext.RichTextData

import java.time.OffsetDateTime

@ConfiguredJsonCodec sealed trait Block

object Block {
  final case class Paragraph(
      id:             String,
      createdTime:    OffsetDateTime,
      lastEditedTime: OffsetDateTime,
      createdBy:      UserId,
      lastEditedBy:   UserId,
      hasChildren:    Boolean,
      archived:       Boolean,
      paragraph:      List[RichTextData.Text] // +color
  ) extends Block

}
