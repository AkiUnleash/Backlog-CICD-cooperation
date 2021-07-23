package controller

import database.MySQLDBImpl
import model.{AccountModel, TriggerModel, WebhookModel}

/** The original trait of the controller */
trait AbstractController extends AccountModel with TriggerModel with WebhookModel with MySQLDBImpl {

  import driver.api._

  /** If the database does not heve a table, create one. */
  def ddl = db.run {
    val schema = TableQuery[TriggerTable].schema ++ TableQuery[AccountTable].schema
    DBIO.seq(schema.create)
  }

}
