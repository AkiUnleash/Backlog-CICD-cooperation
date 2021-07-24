package model

import database.MySQLDBImpl
import spray.json.DefaultJsonProtocol
import java.sql.Date

/** Model of AccountModel data */
trait AccountModel extends DefaultJsonProtocol with AbstractModel {
  this: MySQLDBImpl =>

  import driver.api._

  implicit lazy val AccountFormat = jsonFormat5(Account)
  implicit lazy val AccountLoginFormat = jsonFormat2(AccountLogin)

  /**
   * Class for setting the schema
   *
   * For details on how to describe, see the following URL.
   * http://krrrr38.github.io/slick-doc-ja/v3.0.out/%E3%82%B9%E3%82%AD%E3%83%BC%E3%83%9E.html
   */
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val uuid = column[String]("uuid")
    val backlogSpacekey = column[String]("backlog_spacekey")
    val createAt = column[Date]("create_at")
    val deleteAt = column[Option[Date]]("delete_at")

    def * = (uuid, backlogSpacekey, createAt, deleteAt, id.?) <> (Account.tupled, Account.unapply)
  }
}

/**
 * @param uuid            Generated UUID
 * @param backlogSpacekey Spacekey in Backlog (example: akiunleash)
 * @param createAt        Date when the trigger data was created.
 * @param deleteAt        Date when the trigger data was deleted.
 * @param id              Automatic numbering
 */
case class Account(uuid: String,
                   backlogSpacekey: String,
                   createAt: Date,
                   deleteAt: Option[Date],
                   id: Option[Int] = None)

/**
 * @param backlogSpacekey Spacekey in Backlog (example: akiunleash)
 * @param backlogApikey   Apikey in Backlog (example: eiox93ox...)
 */
case class AccountLogin(backlogSpacekey: String,
                        backlogApikey: String)