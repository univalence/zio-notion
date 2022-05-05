package zio.notion.model.database

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.{Cover, Icon, Parent, UserId}
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.database.description.PropertyDescription

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Database(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      UserId,
    lastEditedBy:   UserId,
    id:             String,
    title:          List[RichTextData.Text],
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, PropertyDescription],
    url:            String
)
