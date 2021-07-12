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


// DefaultJsonProtocolを継承し、JSONを返すようにする
trait TriggerModel extends DefaultJsonProtocol {
  // thisに指定することで、driverを使用に。
  this: MySQLDBImpl =>
  import driver.api._

  implicit object DateFormat extends JsonFormat[Date] {
    val formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    def write(date: Date) = JsString(formatter.format(date))
    def read(value: JsValue) = {
      value match {
        case JsString(date) => new Date(formatter.parse(date).getTime())
        case _ => throw new DeserializationException("Expected JsString")
      }
    }
  }

  // ClassとJSONの変換。フォーマット定義
  implicit lazy val TriggerFormat = jsonFormat10(Trigger)
  implicit lazy val TriggerListFormat = jsonFormat1(TriggerList)
  implicit lazy val TriggerPostFormat = jsonFormat6(TriggerPost)

  // テーブルスキーマの設定
  // 記述方法については以下を参照
  // http://krrrr38.github.io/slick-doc-ja/v3.0.out/%E3%82%B9%E3%82%AD%E3%83%BC%E3%83%9E.html
  class TriggerTable(tag: Tag) extends Table[Trigger](tag, "triggers") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val backlogSpacekey = column[String]("backlog_spacekey")
    val backlogApikey = column[String]("backlog_apikey")
    val backlogIssuekey = column[String]("backlog_issuekey")
    val backlogStatus = column[String]("backlog_status")
    val circleciPipeline = column[String]("circleci_pipeline")
    val circleciApikey = column[String]("circleci_apikey")
    val excuteAt = column[Option[Date]]("executed_at")
    val createAt = column[Date]("create_at")
    val deleteAt = column[Option[Date]]("delete_at")

    def * = (
      backlogSpacekey,
      backlogApikey,
      backlogIssuekey,
      backlogStatus,
      circleciPipeline,
      circleciApikey,
      createAt,
      excuteAt,
      deleteAt,
      id.?) <> (Trigger.tupled, Trigger.unapply)
  }
}

// 使用するケースクラス(データ形式)
case class Trigger(backlogSpacekey: String,
                   backlogApikey: String,
                   backlogIssuekey: String,
                   backlogStatus: String,
                   circleciPipeline: String,
                   circleciApikey: String,
                   createAt: Date,
                   excuteAt: Option[Date],
                   deleteAt: Option[Date],
                   id: Option[Int] = None)
case class TriggerList(accounts: List[Trigger])
case class TriggerPost(backlogSpacekey: String,
                       backlogApikey: String,
                       backlogIssuekey: String,
                       backlogStatus: String,
                       circleciPipeline: String,
                       circleciApikey: String)
