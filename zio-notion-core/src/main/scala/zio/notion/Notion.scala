package zio.notion

import io.circe.Decoder
import io.circe.parser.decode
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError.JsonError
import zio.notion.dsl._
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.Parent.{DatabaseId, PageId}
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.database.{Database, DatabaseQuery}
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.database.query.Query
import zio.notion.model.page.{Page, PatchedProperty}
import zio.notion.model.page.PatchedProperty.PatchedTitle
import zio.notion.model.user.{User, Users}

sealed trait Notion {

  protected def decodeJson[T: Decoder](content: String): IO[NotionError, T] =
    decode[T](content) match {
      case Right(t)    => ZIO.succeed(t)
      case Left(error) => ZIO.fail(JsonError(error))
    }

  def retrievePage(pageId: String): IO[NotionError, Page]
  def retrieveDatabase(databaseId: String): IO[NotionError, Database]
  def retrieveUser(userId: String): IO[NotionError, User]
  def retrieveUsers(pagination: Pagination): IO[NotionError, Users]

  def queryDatabase(databaseId: String, query: Query, pagination: Pagination): IO[NotionError, DatabaseQuery]

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): IO[NotionError, Page]
  def updatePage(page: Page, operations: Page.Patch.Operations): IO[NotionError, Page]

  def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): IO[NotionError, Database]
  def updateDatabase(database: Database, operations: Database.Patch.Operations): IO[NotionError, Database]

  def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  ): IO[NotionError, Database]

  def createPageInPage(
      parent: PageId,
      title: Option[PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover]
  ): IO[NotionError, Page]

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover]
  ): IO[NotionError, Page]

}

object Notion {
  def retrievePage(pageId: String): ZIO[Notion, NotionError, Page]             = ZIO.service[Notion].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: String): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: String): ZIO[Notion, NotionError, User]             = ZIO.service[Notion].flatMap(_.retrieveUser(userId))

  /**
   * Query a batch of users.
   *
   * By default, this query may not retrieve all the users of a database
   * but only a small set of them defined by the Pagination. You still
   * can use [[retrieveAllUsers]] to handle the pagination
   * automatically.
   *
   * @param pagination
   *   The pagination information necessary to the Notion API
   * @return
   *   The result of the query
   */
  def retrieveUsers(pagination: Pagination): ZIO[Notion, NotionError, Users] = ZIO.service[Notion].flatMap(_.retrieveUsers(pagination))

  /**
   * Query all users handling pagination itself.
   *
   * @return
   *   The result of the concatenated queries
   */
  def retrieveAllUsers: ZIO[Notion, NotionError, Users] = {
    val USERS_FETCHED_PER_BATCH = 100

    def retrieveUsersOnceMore(
        maybeStartCursor: Option[String],
        users: Seq[User]
    ): ZIO[Notion, NotionError, Seq[User]] =
      maybeStartCursor match {
        case Some(startCursor) =>
          for {
            result <- retrieveUsers(Pagination(USERS_FETCHED_PER_BATCH, Some(startCursor)))
            users  <- retrieveUsersOnceMore(result.nextCursor, users ++ result.results)
          } yield users
        case None => ZIO.succeed(users)
      }

    for {
      firstQuery <- retrieveUsers(Pagination.start(USERS_FETCHED_PER_BATCH))
      allUsers   <- retrieveUsersOnceMore(firstQuery.nextCursor, firstQuery.results)
    } yield Users(allUsers, None)
  }

  /**
   * Query a batch of pages of a database base on a query.
   *
   * For the query parameter, due to implicit conversion, you can
   * specify either a Sorts or a Query or combine both using the combine
   * operator available on both sorts or filter set.
   *
   * By default, this query may not retrieve all the pages of a database
   * but only a small set of them defined by the Pagination. You still
   * can use [[queryAllDatabase]] to handle the pagination
   * automatically.
   *
   * @param databaseId
   *   The database's identifier
   * @param query
   *   The query containing the sorts and the filter formula
   * @param pagination
   *   The pagination information necessary to the Notion API
   * @return
   *   The result of the query
   */
  def queryDatabase(
      databaseId: String,
      query: Query,
      pagination: Pagination
  ): ZIO[Notion, NotionError, DatabaseQuery] = ZIO.service[Notion].flatMap(_.queryDatabase(databaseId, query, pagination))

  /**
   * Query all pages of a database handling pagination itself.
   *
   * @param databaseId
   *   The database's ID
   * @param query
   *   The query containing the sorts and the filter formula
   * @return
   *   The result of the concatenated queries
   */
  def queryAllDatabase(
      databaseId: String,
      query: Query = Query.empty
  ): ZIO[Notion, NotionError, DatabaseQuery] = {
    val PAGES_FETCHED_PER_BATCH = 100

    def queryDatabaseOnceMore(
        query: Query,
        maybeStartCursor: Option[String],
        pages: Seq[Page]
    ): ZIO[Notion, NotionError, Seq[Page]] =
      maybeStartCursor match {
        case Some(startCursor) =>
          for {
            result <- queryDatabase(databaseId, query, Pagination(PAGES_FETCHED_PER_BATCH, Some(startCursor)))
            pages  <- queryDatabaseOnceMore(query, result.nextCursor, pages ++ result.results)
          } yield pages
        case None => ZIO.succeed(pages)
      }

    for {
      firstQuery <- queryDatabase(databaseId, query, Pagination.start(PAGES_FETCHED_PER_BATCH))
      allPages   <- queryDatabaseOnceMore(query, firstQuery.nextCursor, firstQuery.results)
    } yield DatabaseQuery(allPages, None)
  }

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): ZIO[Notion, NotionError, Page] =
    ZIO.service[Notion].flatMap(_.updatePage(pageId, operations))

  def updatePage(page: Page, operations: Page.Patch.Operations): ZIO[Notion, NotionError, Page] =
    ZIO.service[Notion].flatMap(_.updatePage(page, operations))

  def updatePage(pageId: String, operation: Page.Patch.Operations.Operation.Stateless): ZIO[Notion, NotionError, Page] =
    updatePage(pageId, Page.Patch.StatelessOperations(List(operation)))

  def updatePage(page: Page, operation: Page.Patch.Operations.Operation): ZIO[Notion, NotionError, Page] =
    updatePage(page, Page.Patch.Operations(List(operation)))

  def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): ZIO[Notion, NotionError, Database] =
    ZIO.service[Notion].flatMap(_.updateDatabase(databaseId, operations))

  def updateDatabase(database: Database, operations: Database.Patch.Operations): ZIO[Notion, NotionError, Database] =
    ZIO.service[Notion].flatMap(_.updateDatabase(database, operations))

  def updateDatabase(databaseId: String, operation: Database.Patch.Operations.Operation.Stateless): ZIO[Notion, NotionError, Database] =
    updateDatabase(databaseId, Database.Patch.StatelessOperations(List(operation)))

  def updateDatabase(database: Database, operation: Database.Patch.Operations.Operation): ZIO[Notion, NotionError, Database] =
    updateDatabase(database, Database.Patch.Operations(List(operation)))

  def deletePage(pageId: String): ZIO[Notion, NotionError, Unit] = Notion.updatePage(pageId, archive).unit

  def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  ): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.createDatabase(pageId, title, icon, cover, properties))

  def createPageInPage(
      parent: PageId,
      title: Option[PatchedTitle],
      icon: Option[Icon],
      cover: Option[Cover]
  ): ZIO[Notion, NotionError, Page] = ZIO.service[Notion].flatMap(_.createPageInPage(parent, title, icon, cover))

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover]
  ): ZIO[Notion, NotionError, Page] = ZIO.service[Notion].flatMap(_.createPageInDatabase(parent, properties, icon, cover))

  val live: URLayer[NotionClient, Notion] = ZLayer(ZIO.service[NotionClient].map(LiveNotion))

  def layerWith(bearer: String): Layer[Throwable, Notion] =
    AsyncHttpClientZioBackend.layer() ++ ZLayer.succeed(NotionConfiguration(bearer)) >>> NotionClient.live >>> Notion.live

  final case class LiveNotion(notionClient: NotionClient) extends Notion {
    private def decodeResponse[T: Decoder](request: IO[NotionError, NotionResponse]): IO[NotionError, T] = request.flatMap(decodeJson[T])

    override def retrievePage(pageId: String): IO[NotionError, Page] = decodeResponse[Page](notionClient.retrievePage(pageId))

    override def retrieveDatabase(databaseId: String): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.retrieveDatabase(databaseId))

    override def retrieveUser(userId: String): IO[NotionError, User] = decodeResponse[User](notionClient.retrieveUser(userId))

    override def retrieveUsers(pagination: Pagination): IO[NotionError, Users] =
      decodeResponse[Users](notionClient.retrieveUsers(pagination))

    override def queryDatabase(databaseId: String, query: Query, pagination: Pagination): IO[NotionError, DatabaseQuery] =
      decodeResponse[DatabaseQuery](notionClient.queryDatabase(databaseId, query, pagination))

    override def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.updatePage(pageId, operations))

    override def updatePage(page: Page, operations: Page.Patch.Operations): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.updatePage(page, operations))

    override def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.updateDatabase(databaseId, operations))

    override def updateDatabase(database: Database, operations: Database.Patch.Operations): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.updateDatabase(database, operations))

    override def createDatabase(
        pageId: String,
        title: Seq[RichTextData],
        icon: Option[Icon],
        cover: Option[Cover],
        properties: Map[String, PropertySchema]
    ): IO[NotionError, Database] = decodeResponse[Database](notionClient.createDatabase(pageId, title, icon, cover, properties))

    override def createPageInPage(
        parent: PageId,
        title: Option[PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover]
    ): IO[NotionError, Page] = decodeResponse[Page](notionClient.createPageInPage(parent, title, icon, cover))

    override def createPageInDatabase(
        parent: DatabaseId,
        properties: Map[String, PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover]
    ): IO[NotionError, Page] = decodeResponse[Page](notionClient.createPageInDatabase(parent, properties, icon, cover))
  }
}
