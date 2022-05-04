package zio.notion.model

import java.time.OffsetDateTime

abstract class Block(id: String)

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