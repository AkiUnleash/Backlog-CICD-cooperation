package database

import slick.jdbc.MySQLProfile

/** Use MySQL with Slick. */
trait MySQLDBImpl {
  val driver = MySQLProfile

  import driver.api.Database

  val db: Database = MySqlDB.connectionPool
}

/** Use resources/application.conf connection infomation.
 *
 * {{{
 * mysql = {
 *      dataSourceClass="com.mysql.cj.jdbc.MysqlDataSource"
 *      roperties {
 *      ser="root"
 *      assword="scala"
 *      atabaseName="db"
 *      erverName="localhost"
 *      ortNumber="3306"
 *   }
 *   numThreads=1
 * }}}
 */
private object MySqlDB {

  import slick.jdbc.MySQLProfile.api._

  val connectionPool = Database.forConfig("mysql")
}