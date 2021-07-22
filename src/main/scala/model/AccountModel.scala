package model

import database.MySQLDBImpl
import spray.json.DefaultJsonProtocol
import spray.json.DeserializationException
import spray.json.JsString
import spray.json.JsValue
import spray.json.JsonFormat

import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID
import java.sql.Date
import model.AbstractModel

// DefaultJsonProtocolを継承し、JSONを返すようにする
trait AccountModel extends DefaultJsonProtocol with AbstractModel {
  // thisに指定することで、driverを使用に。
  this: MySQLDBImpl =>
  import driver.api._

  // ClassとJSONの変換。フォーマット定義
  implicit lazy val AccountFormat = jsonFormat5(Account)
  implicit lazy val AccountLoginFormat = jsonFormat2(AccountLogin)

  // テーブルスキーマの設定
  // 記述方法については以下を参照
  // http://krrrr38.github.io/slick-doc-ja/v3.0.out/%E3%82%B9%E3%82%AD%E3%83%BC%E3%83%9E.html
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val uuid = column[String]("uuid")
    val backlogSpacekey = column[String]("backlog_spacekey")
    val createAt = column[Date]("create_at")
    val deleteAt = column[Option[Date]]("delete_at")

    def * = ( uuid, backlogSpacekey, createAt, deleteAt, id.?) <>(Account.tupled, Account.unapply)
  }
}

// 使用するケースクラス(データ形式)
case class Account(uuid: String, backlogSpacekey: String, createAt: Date, deleteAt: Option[Date], id: Option[Int] = None)
case class AccountLogin(backlogSpacekey: String, backlogApikey: String)