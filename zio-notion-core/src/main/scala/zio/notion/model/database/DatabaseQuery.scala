package zio.notion.model.database

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.page.Page

@ConfiguredJsonCodec
final case class DatabaseQuery(nextCursor: Option[String], results: List[Page])
