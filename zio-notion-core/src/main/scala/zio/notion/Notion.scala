package zio.notion

import io.circe.Decoder
import io.circe.parser.decode
import sttp.client3.httpclient.zio.HttpClientZioBackend

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError.JsonError
import zio.notion.dsl._
import zio.notion.model.block.{Block, BlockContent, Blocks}
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.Parent.{DatabaseId, PageId}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.database.{Database, DatabaseQuery}
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.database.query.Query
import zio.notion.model.page.{Page, PatchedProperty}
import zio.notion.model.page.PatchedProperty.PatchedTitle
import zio.notion.model.user.{User, Users}

sealed trait Notion {

  protected def decodeJson[T: Decoder](content: String)(implicit trace: Trace): IO[NotionError, T] =
    decode[T](content) match {
      case Right(t)    => ZIO.succeed(t)
      case Left(error) => ZIO.fail(JsonError(error))
    }

  def retrievePage(pageId: String)(implicit trace: Trace): IO[NotionError, Page]
  def retrieveDatabase(databaseId: String)(implicit trace: Trace): IO[NotionError, Database]
  def retrieveUser(userId: String)(implicit trace: Trace): IO[NotionError, User]
  def retrieveUsers(pagination: Pagination)(implicit trace: Trace): IO[NotionError, Users]
  def retrieveBlock(blockId: String)(implicit trace: Trace): IO[NotionError, Block]
  def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): IO[NotionError, Blocks]

  def queryDatabase(databaseId: String, query: Query, pagination: Pagination)(implicit trace: Trace): IO[NotionError, DatabaseQuery]

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations)(implicit trace: Trace): IO[NotionError, Page]
  def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): IO[NotionError, Page]

  def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations)(implicit trace: Trace): IO[NotionError, Database]
  def updateDatabase(database: Database, operations: Database.Patch.Operations)(implicit trace: Trace): IO[NotionError, Database]

  def appendBlocks(blockId: String, blocks: List[BlockContent])(implicit trace: Trace): IO[NotionError, Blocks]

  def createDatabase(
      pageId: String,
      title: Seq[RichTextFragment],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  )(implicit trace: Trace): IO[NotionError, Database]

  def createPageInPage(
      parent: PageId,
      title: Option[PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, Page]

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, Page]

}

object Notion {

  def retrievePage(pageId: String)(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    ZIO.serviceWithZIO[Notion](_.retrievePage(pageId))

  def retrieveDatabase(databaseId: String)(implicit trace: Trace): ZIO[Notion, NotionError, Database] =
    ZIO.serviceWithZIO[Notion](_.retrieveDatabase(databaseId))

  def retrieveUser(userId: String)(implicit trace: Trace): ZIO[Notion, NotionError, User] =
    ZIO.serviceWithZIO[Notion](_.retrieveUser(userId))

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
  def retrieveUsers(pagination: Pagination)(implicit trace: Trace): ZIO[Notion, NotionError, Users] =
    ZIO.serviceWithZIO[Notion](_.retrieveUsers(pagination))

  /**
   * Query all users handling pagination itself.
   *
   * @return
   *   The result of the concatenated queries
   */
  def retrieveAllUsers(implicit trace: Trace): ZIO[Notion, NotionError, Users] = {
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

  def retrieveBlock(blockId: String)(implicit trace: Trace): ZIO[Notion, NotionError, Block] =
    ZIO.serviceWithZIO[Notion](_.retrieveBlock(blockId))

  /**
   * Query a batch of blocks.
   *
   * By default, this query may not retrieve all the blocks of a page
   * but only a small set of them defined by the Pagination. You still
   * can use [[retrieveAllBlocks]] to handle the pagination
   * automatically.
   *
   * @param pageId
   *   The page id containing the blocks
   * @param pagination
   *   The pagination information necessary to the Notion API
   * @return
   *   The result of the query
   */
  def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): ZIO[Notion, NotionError, Blocks] =
    ZIO.serviceWithZIO[Notion](_.retrieveBlocks(pageId, pagination))

  /**
   * Query all users handling pagination itself.
   *
   * @return
   *   The result of the concatenated queries
   */
  def retrieveAllBlocks(pageId: String)(implicit trace: Trace): ZIO[Notion, NotionError, Blocks] = {
    val BLOCKS_FETCHED_PER_BATCH = 100

    def retrieveBlocksOnceMore(
        maybeStartCursor: Option[String],
        blocks: Seq[Block]
    ): ZIO[Notion, NotionError, Seq[Block]] =
      maybeStartCursor match {
        case Some(startCursor) =>
          for {
            result <- retrieveBlocks(pageId, Pagination(BLOCKS_FETCHED_PER_BATCH, Some(startCursor)))
            blocks <- retrieveBlocksOnceMore(result.nextCursor, blocks ++ result.results)
          } yield blocks
        case None => ZIO.succeed(blocks)
      }

    for {
      firstQuery <- retrieveBlocks(pageId, Pagination.start(BLOCKS_FETCHED_PER_BATCH))
      allBlocks  <- retrieveBlocksOnceMore(firstQuery.nextCursor, firstQuery.results)
    } yield Blocks(allBlocks, None)
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
  )(implicit trace: Trace): ZIO[Notion, NotionError, DatabaseQuery] =
    ZIO.serviceWithZIO[Notion](_.queryDatabase(databaseId, query, pagination))

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
  )(implicit trace: Trace): ZIO[Notion, NotionError, DatabaseQuery] = {
    val PAGES_FETCHED_PER_BATCH = 100

    def queryDatabaseOnceMore(
        maybeStartCursor: Option[String],
        pages: Seq[Page]
    ): ZIO[Notion, NotionError, Seq[Page]] =
      maybeStartCursor match {
        case Some(startCursor) =>
          for {
            result <- queryDatabase(databaseId, query, Pagination(PAGES_FETCHED_PER_BATCH, Some(startCursor)))
            pages  <- queryDatabaseOnceMore(result.nextCursor, pages ++ result.results)
          } yield pages
        case None => ZIO.succeed(pages)
      }

    for {
      firstQuery <- queryDatabase(databaseId, query, Pagination.start(PAGES_FETCHED_PER_BATCH))
      allPages   <- queryDatabaseOnceMore(firstQuery.nextCursor, firstQuery.results)
    } yield DatabaseQuery(allPages, None)
  }

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations)(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    ZIO.serviceWithZIO[Notion](_.updatePage(pageId, operations))

  def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    ZIO.serviceWithZIO[Notion](_.updatePage(page, operations))

  def updatePage(
      pageId: String,
      operation: Page.Patch.Operations.Operation.Stateless
  )(implicit trace: Trace): ZIO[Notion, NotionError, Page] = updatePage(pageId, Page.Patch.StatelessOperations(List(operation)))

  def updatePage(page: Page, operation: Page.Patch.Operations.Operation)(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    updatePage(page, Page.Patch.Operations(List(operation)))

  def updateDatabase(
      databaseId: String,
      operations: Database.Patch.StatelessOperations
  )(implicit trace: Trace): ZIO[Notion, NotionError, Database] = ZIO.serviceWithZIO[Notion](_.updateDatabase(databaseId, operations))

  def updateDatabase(database: Database, operations: Database.Patch.Operations)(implicit trace: Trace): ZIO[Notion, NotionError, Database] =
    ZIO.serviceWithZIO[Notion](_.updateDatabase(database, operations))

  def updateDatabase(
      databaseId: String,
      operation: Database.Patch.Operations.Operation.Stateless
  )(implicit trace: Trace): ZIO[Notion, NotionError, Database] =
    updateDatabase(databaseId, Database.Patch.StatelessOperations(List(operation)))

  def updateDatabase(
      database: Database,
      operation: Database.Patch.Operations.Operation
  )(implicit trace: Trace): ZIO[Notion, NotionError, Database] = updateDatabase(database, Database.Patch.Operations(List(operation)))

  def appendBlocks(blockId: NotionResponse, blocks: List[BlockContent])(implicit trace: Trace): ZIO[Notion, NotionError, Blocks] =
    ZIO.serviceWithZIO[Notion](_.appendBlocks(blockId, blocks))

  def deletePage(pageId: String)(implicit trace: Trace): ZIO[Notion, NotionError, Unit] = Notion.updatePage(pageId, archive).unit

  def createDatabase(
      pageId: String,
      title: Seq[RichTextFragment],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  )(implicit trace: Trace): ZIO[Notion, NotionError, Database] =
    ZIO.serviceWithZIO[Notion](_.createDatabase(pageId, title, icon, cover, properties))

  def createPageInPage(
      parent: PageId,
      title: Option[PatchedTitle],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    ZIO.serviceWithZIO[Notion](_.createPageInPage(parent, title, icon, cover, children))

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): ZIO[Notion, NotionError, Page] =
    ZIO.serviceWithZIO[Notion](_.createPageInDatabase(parent, properties, icon, cover, children))

  val live: URLayer[NotionClient, Notion] = ZLayer(ZIO.service[NotionClient].map(LiveNotion))

  def layerWith(bearer: String): Layer[Throwable, Notion] =
    HttpClientZioBackend.layer() ++ ZLayer.succeed(NotionConfiguration(bearer)) >>> NotionClient.live >>> Notion.live

  final case class LiveNotion(notionClient: NotionClient) extends Notion {
    private def decodeResponse[T: Decoder](request: IO[NotionError, NotionResponse]): IO[NotionError, T] = request.flatMap(decodeJson[T])

    override def retrievePage(pageId: String)(implicit trace: Trace): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.retrievePage(pageId))

    override def retrieveDatabase(databaseId: String)(implicit trace: Trace): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.retrieveDatabase(databaseId))

    override def retrieveUser(userId: String)(implicit trace: Trace): IO[NotionError, User] =
      decodeResponse[User](notionClient.retrieveUser(userId))

    override def retrieveUsers(pagination: Pagination)(implicit trace: Trace): IO[NotionError, Users] =
      decodeResponse[Users](notionClient.retrieveUsers(pagination))

    override def retrieveBlock(blockId: String)(implicit trace: Trace): IO[NotionError, Block] =
      decodeResponse[Block](notionClient.retrieveBlock(blockId))

    def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): IO[NotionError, Blocks] =
      decodeResponse[Blocks](notionClient.retrieveBlocks(pageId, pagination))

    override def queryDatabase(
        databaseId: String,
        query: Query,
        pagination: Pagination
    )(implicit trace: Trace): IO[NotionError, DatabaseQuery] =
      decodeResponse[DatabaseQuery](notionClient.queryDatabase(databaseId, query, pagination))

    override def updatePage(pageId: String, operations: Page.Patch.StatelessOperations)(implicit trace: Trace): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.updatePage(pageId, operations))

    override def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.updatePage(page, operations))

    override def updateDatabase(
        databaseId: String,
        operations: Database.Patch.StatelessOperations
    )(implicit trace: Trace): IO[NotionError, Database] = decodeResponse[Database](notionClient.updateDatabase(databaseId, operations))

    override def updateDatabase(
        database: Database,
        operations: Database.Patch.Operations
    )(implicit trace: Trace): IO[NotionError, Database] = decodeResponse[Database](notionClient.updateDatabase(database, operations))

    override def appendBlocks(blockId: NotionResponse, blocks: List[BlockContent])(implicit trace: Trace): IO[NotionError, Blocks] =
      decodeResponse[Blocks](notionClient.appendBlocks(blockId, blocks))

    override def createDatabase(
        pageId: String,
        title: Seq[RichTextFragment],
        icon: Option[Icon],
        cover: Option[Cover],
        properties: Map[String, PropertySchema]
    )(implicit trace: Trace): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.createDatabase(pageId, title, icon, cover, properties))

    override def createPageInPage(
        parent: PageId,
        title: Option[PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover],
        children: Seq[BlockContent]
    )(implicit trace: Trace): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.createPageInPage(parent, title, icon, cover, children))

    override def createPageInDatabase(
        parent: DatabaseId,
        properties: Map[String, PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover],
        children: Seq[BlockContent]
    )(implicit trace: Trace): IO[NotionError, Page] =
      decodeResponse[Page](notionClient.createPageInDatabase(parent, properties, icon, cover, children))
  }
}
