package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class PersonData(email: Option[String])
