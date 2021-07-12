package database

import slick.jdbc.MySQLProfile

// Connect to Database
trait MySQLDBImpl {
  val driver = MySQLProfile
  import driver.api.Database
  val db: Database = MySqlDB.connectionPool
}

// Refer to resources/application.conf
private object MySqlDB {
  import slick.jdbc.MySQLProfile.api._
  val connectionPool = Database.forConfig("mysql")
}