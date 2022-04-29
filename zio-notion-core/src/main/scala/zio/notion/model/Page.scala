package zio.notion.model

import zio.json._

import java.time.OffsetDateTime

case class Page(
    @jsonField("created_time") createdTime:        OffsetDateTime,
    @jsonField("last_edited_time") lastEditedTime: OffsetDateTime,
    @jsonField("created_by") createdBy:            User,
    @jsonField("last_edited_by") lastEditedBy:     User,
    id:                                            String,
    cover:                                         Option[Cover],
    icon:                                          Option[Icon],
    parent:                                        Parent,
    archived:                                      Boolean,
    // properties:                                   Seq[Any],
    url: String
)

object Page {
  implicit val decoder: JsonDecoder[Page] = DeriveJsonDecoder.gen[Page]
  implicit val encoder: JsonEncoder[Page] = DeriveJsonEncoder.gen[Page]
}
