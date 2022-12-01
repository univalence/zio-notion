package example

import sttp.client3.httpclient.zio.HttpClientZioBackend

import zio._
import zio.notion._
import zio.notion.NotionError.PropertyWrongType
import zio.notion.dsl.{euro, ColumnContext}
import zio.notion.model.database.Database
import zio.notion.model.page.{Page, Property}

/**
 * in this example we start off with this kind of database :
 * | Name      | Balance (in $) | Balance (in €) |
 * |:----------|:---------------|:---------------|
 * | balance 1 | 1$             |                |
 *
 * and want to end up with this :
 *
 * | Name      | Balance (in $) | Balance (in €) |
 * |:----------|:---------------|:---------------|
 * | balance 1 | 1$             | X€             |
 */
object UpdateDatabase extends ZIOAppDefault {

  /**
   * Gets, for one page in a database, the value of a number column
   * called "Balance (in $)" just like in the example below.
   *
   * Page:
   * | Name      | Balance (in $) |
   * |:----------|:---------------|
   * | balance 1 | 1$             |
   */
  private def getBalance(page: Page): ZIO[Any, NotionError, Double] =
    page.properties("Balance (in $)") match {
      case Property.Number(_, number) => ZIO.succeed(number.getOrElse(0))
      case _                          => ZIO.fail(PropertyWrongType("Balance", "number", "well not the right type"))
    }

  /**
   * Returns an effect that updates the Balance (in €) column.
   *
   * Before:
   * | Name      | Balance (in $) | Balance (in €) |
   * |:----------|:---------------|:---------------|
   * | balance 1 | 1$             |                |
   *
   * After:
   * | Name      | Balance (in $) | Balance (in €) |
   * |:----------|:---------------|:---------------|
   * | balance 1 | 1$             | X€             |
   */
  private def updateBalanceInEuroColumn(page: Page, conversionRate: Double): ZIO[Notion, NotionError, Unit] =
    for {
      balance <- getBalance(page)
      balanceInEuro = $"Balance (in €)".asNumber.patch.set(balance * conversionRate)
      _ <- Notion.updatePage(page, balanceInEuro)
    } yield ()

  private def addBalanceInEuroColumn(databaseId: String): ZIO[Notion, NotionError, Database] = {
    val balanceInEuroColumn = $$"Balance (in €)".create.as(euro)

    Notion.updateDatabase(databaseId, balanceInEuroColumn)
  }

  private def retrieveDatabaseRows(databaseId: String): ZIO[Notion, NotionError, Seq[Page]] =
    Notion.queryAllDatabase(databaseId).map(_.results)

  def example: ZIO[Notion with ExchangeRateAPI, NotionError, Unit] = {
    val databaseId = "***" // Insert your own page ID

    for {
      rates          <- ZIO.service[ExchangeRateAPI]
      conversionRate <- rates.getUSDtoEURRate.orDie
      _              <- addBalanceInEuroColumn(databaseId)
      rows           <- retrieveDatabaseRows(databaseId)
      effects = rows.map(updateBalanceInEuroColumn(_, conversionRate))
      _ <- ZIO.collectAllPar(effects)

    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(
      Notion.layerWith("secret_***"), // Insert your own bearer
      HttpClientZioBackend.layer(),
      ExchangeRateAPI.live
    )

}
