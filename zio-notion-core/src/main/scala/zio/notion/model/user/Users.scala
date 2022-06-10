package zio.notion.model.user

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class Users(results: Seq[User], nextCursor: Option[String])
