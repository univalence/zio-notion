package zio.notion.model.database

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.page.Page

@ConfiguredJsonCodec(decodeOnly = true)
final case class DatabaseQuery(results: Seq[Page], nextCursor: Option[String])
