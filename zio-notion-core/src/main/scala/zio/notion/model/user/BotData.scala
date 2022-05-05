package zio.notion.model.user

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait BotData

object BotData {
  final case class Bot(owner: User) extends BotData
  final case object EmptyObject     extends BotData
}
