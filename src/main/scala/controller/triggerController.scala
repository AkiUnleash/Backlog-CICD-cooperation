package controller

import database.MySQLDBImpl
import model.{TriggerModel, Trigger}

import java.sql.Date
import scala.concurrent.Future

trait triggerController  extends TriggerModel  with MySQLDBImpl {

  import driver.api._

  protected val TriggerTableQuery = TableQuery[TriggerTable]

  // 起動時にスキーマを元にテーブルを作成する処理
  def ddl = db.run {
    TriggerTableQuery.schema.create
  }

  // アカウント追加処理
  def create(trigger: Trigger): Future[Int] = db.run {
    TriggerTableAutoInc += trigger
  }

//  // アカウントからユーザー取得（E-mail指定）
//  def getByUser(email: String): Future[Option[Account]] = db.run {
//    AccountTableQuery.filter(_.email === email).result.headOption
//  }
//
//  // アカウントからユーザー取得（UUID指定）
//  def getByUserUuid(uuid: String): Future[Option[Account]] = db.run {
//    AccountTableQuery.filter(_.uuid === uuid).result.headOption
//  }
//
//  // アカウントの更新
//  def updateUser(uuid: String, UpdatePost: UpdatePost): Future[Int] = db.run {
//    val q = for { l <- AccountTableQuery if l.uuid === uuid } yield (l.username, l.email)
//    q.update((UpdatePost.username, UpdatePost.email))
//  }
//
//  // アカウントの削除（削除フラグの追加）
//  def deleteUser(uuid: String): Future[Int] = db.run {
//    val currentDate = new Date(System.currentTimeMillis())
//    val q = for { l <- AccountTableQuery if l.uuid === uuid } yield l.deleteAt
//    q.update(Option(currentDate))
//  }
//
  def TriggerTableAutoInc =
    TriggerTableQuery returning TriggerTableQuery.map(_.id)
}