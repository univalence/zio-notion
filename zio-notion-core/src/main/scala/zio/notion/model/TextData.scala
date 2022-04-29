package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec final case class TextData(content: String, link: Option[Url])
