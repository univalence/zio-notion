package example

import io.circe.{Decoder, HCursor}
import io.circe.parser.decode
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.{basicRequest, Request, Response, SttpBackend, UriContext}

import zio.{Task, URLayer, ZIO, ZLayer}

final case class ERPayload(eurRate: Double)

object ERPayload {

  implicit val decoder: Decoder[ERPayload] =
    (c: HCursor) =>
      for {
        eur <- c.downField("rates").downField("EUR").as[Double]
      } yield ERPayload(eur)
}

sealed trait ExchangeRateAPI {
  def getUSDtoEURRate: ZIO[Any, Throwable, Double]
}

object ExchangeRateAPI {

  val live: URLayer[SttpBackend[Task, ZioStreams with WebSockets], ExchangeRateAPI] =
    ZLayer {
      for {
        backend <- ZIO.service[SttpBackend[Task, ZioStreams with WebSockets]]
      } yield LiveER(backend)
    }
}

final case class LiveER(backend: SttpBackend[Task, ZioStreams with WebSockets]) extends ExchangeRateAPI {
  val request: Request[Either[String, String], Any]  = basicRequest.get(uri"https://open.er-api.com/v6/latest/USD")
  val effect: Task[Response[Either[String, String]]] = backend.send(request)

  override def getUSDtoEURRate: ZIO[Any, Throwable, Double] =
    effect.flatMap { response =>
      response.body match {
        case Right(body) =>
          decode[ERPayload](body) match {
            case Left(_)      => ZIO.fail(new NullPointerException)
            case Right(value) => ZIO.succeed(value.eurRate)
          }
        case Left(_) =>
          ZIO.fail(new NullPointerException)
      }
    }
}
