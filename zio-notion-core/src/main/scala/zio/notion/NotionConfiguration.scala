package zio.notion

import zio.{ULayer, ZLayer}

final case class NotionConfiguration(bearer: String) { self =>
  def asLayer: ULayer[NotionConfiguration] = ZLayer.succeed(self)
}
