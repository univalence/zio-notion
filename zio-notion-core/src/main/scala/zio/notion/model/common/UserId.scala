package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import java.util.UUID

@ConfiguredJsonCodec final case class UserId(id: UUID)
