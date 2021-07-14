package controller

import database.MySQLDBImpl
import model.{AccountModel, Account}

import java.sql.Date
import scala.concurrent.Future

trait accountController extends abstractController {

  import driver.api._

  protected val AccountTableQuery = TableQuery[AccountTable]


  // アカウント追加処理
  def create(account: Account): Future[Int] = db.run {
    AccountTableAutoInc += account
  }

  // アカウントからユーザー取得（E-mail指定）
  def getByUser(backlogSpacekey: String): Future[Option[Account]] = db.run {
    AccountTableQuery.filter(_.backlogSpacekey === backlogSpacekey).result.headOption
  }

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

  def AccountTableAutoInc =
    AccountTableQuery returning AccountTableQuery.map(_.id)
}