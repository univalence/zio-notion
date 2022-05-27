package zio.notion.model.user

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec case class Users(
    results:    Seq[User],
    nextCursor: String,
    hasMore:    Boolean
)
