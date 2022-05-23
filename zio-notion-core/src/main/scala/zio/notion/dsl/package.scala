package zio.notion

package object dsl {
  implicit class ColumnContext(val sc: StringContext) {
    def $(args: Any*): Column = col(sc.s(args: _*))

    def $$(args: Any*): ColumnDefinition = colDefinition(sc.s(args: _*))
  }

  def columnsMatching(predicate: String => Boolean): Columns = Columns(predicate)

  val allColumns: Columns = columnsMatching(_ => true)

  def columnDefinitionsMatching(predicate: String => Boolean): ColumnDefinitions = ColumnDefinitions(predicate)

  val allColumnDefinitions: ColumnDefinitions = columnDefinitionsMatching(_ => true)

  def col(colName: String): Column = Column(colName)

  def colDefinition(colName: String): ColumnDefinition = ColumnDefinition(colName)
}
