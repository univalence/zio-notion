package zio.notion.dsl

trait ColumnDSL {

  implicit class ColumnContext(val sc: StringContext) {
    def $(args: Any*): Column = col(sc.s(args: _*))

    def $$(args: Any*): ColumnDefinition = colDefinition(sc.s(args: _*))
  }

  def col(colName: String): Column = Column(colName)

  def colDefinition(colName: String): ColumnDefinition = ColumnDefinition(colName)
}

object ColumnDSL extends ColumnDSL
