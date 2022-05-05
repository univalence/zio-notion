package zio.notion.model.page

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.{Cover, Icon, Parent, UserId}
import zio.notion.model.page.properties.Property

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
)
