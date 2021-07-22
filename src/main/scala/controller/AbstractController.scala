package controller

import database.MySQLDBImpl
import model.{AccountModel, TriggerModel, WebhookModel}

trait AbstractController extends AccountModel with TriggerModel with WebhookModel with MySQLDBImpl {

  import driver.api._

  //   起動時にスキーマを元にテーブルを作成する処理
  def ddl = db.run {
    val schema = TableQuery[TriggerTable].schema ++ TableQuery[AccountTable].schema
    DBIO.seq( schema.create )
  }
}
