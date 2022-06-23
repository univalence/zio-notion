package zio.notion.model.block

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class Blocks(results: Seq[Block], nextCursor: Option[String])
