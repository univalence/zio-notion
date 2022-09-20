package example

import sttp.capabilities
import sttp.capabilities.zio.ZioStreams
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

import zio._
import zio.notion._
import zio.notion.NotionError.PropertyWrongType
import zio.notion.dsl.{euro, ColumnContext}
import zio.notion.model.page.{Page, Property}
import zio.notion.model.page.Page.Patch.Operations.Operation

/**
 * in this example we start off with this kind of database :
 * | Name      | Balance |
 * |:----------|:--------|
 * | balance 1 | 1$      |
 *
 * and want to end up with this :
 *
 * | Name      | Balance | ToEuros |
 * |:----------|:--------|:--------|
 * | balance 1 | 1$      | X€      |
 */
object UpdateDatabase extends ZIOAppDefault {

  /**
   * Gets, for one page in a database, the value of a number column
   * called "Balance" just like in the example below
   *
   * Page:
   * | Name      | Balance |
   * |:----------|:--------|
   * | balance 1 | 1$      |
   *
   * Returns an effect (that should return 1D)
   */
  def getBalance(page: Page): ZIO[Any, NotionError, Double] =
    page.properties("Balance") match {
      case Property.Number(_, number) => ZIO.succeed(number.getOrElse(0))
      case _                          => ZIO.fail(PropertyWrongType("Balance", "number", "well not the right type"))
    }

  /**
   * Returns an effect that updates the ToEuros column
   *
   * Before:
   * | Name      | Balance | ToEuros |
   * |:----------|:--------|:--------|
   * | balance 1 | 1$      |         |
   *
   * After:
   * | Name      | Balance | ToEuros |
   * |:----------|:--------|:--------|
   * | balance 1 | 1$      | X€      |
   */
  def updateToEurosColumn(p: Page, conv: Double): ZIO[Notion, NotionError, Unit] = {
    def convert(dollarValue: Double, conversion: Double): Operation.SetProperty = $"ToEuros".asNumber.patch.set(dollarValue * conversion)

    for {
      balance <- getBalance(p)
      _       <- Notion.updatePage(p, convert(balance, conv))
    } yield ()
  }

  def example: ZIO[Notion with ExchangeRateAPI, NotionError, Unit] = {
    val createToEurosColumn = $$"ToEuros".create.as(euro)
    val dbId                = "xxx" // Insert your own page ID

    for {
      rates    <- ZIO.service[ExchangeRateAPI]
      convRate <- rates.getUSDtoEURRate.orDie
      _        <- Notion.updateDatabase(dbId, createToEurosColumn) // creates a column named "ToEuros"
      db       <- Notion.queryAllDatabase(dbId)                    // returns the whole database (all pages included)
      effects = db.results.map(updateToEurosColumn(_, convRate))
      _ <- ZIO.collectAllPar(effects)

    } yield ()
  }

  val sttpLayer: Layer[Throwable, SttpBackend[Task, ZioStreams with capabilities.WebSockets]] = AsyncHttpClientZioBackend.layer()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example
      .tapError(e => Console.printLine(e.humanize))
      .provide(
        Notion.layerWith("secret_xxx"), // Insert your own bearer
        sttpLayer,
        ExchangeRateAPI.live
      )

}
