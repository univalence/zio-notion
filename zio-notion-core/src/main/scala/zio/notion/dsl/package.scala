package zio.notion

package object dsl {
  implicit class ColumnContext(val sc: StringContext) {
    def $(args: Any*): Column = Column(sc.s(args: _*))
  }

  def col(colName: String): Column = Column(colName)
}
