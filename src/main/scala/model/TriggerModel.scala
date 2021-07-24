package model

import database.MySQLDBImpl
import spray.json.DefaultJsonProtocol
import java.sql.Date

/** Model of trigger data */
trait TriggerModel extends DefaultJsonProtocol with AbstractModel {
  this: MySQLDBImpl =>

  import driver.api._

  implicit lazy val TriggerFormat = jsonFormat11(Trigger)
  implicit lazy val TriggerListFormat = jsonFormat1(TriggerList)
  implicit lazy val TriggerPostFormat = jsonFormat6(TriggerPost)

  /**
   * Class for setting the schema
   *
   * For details on how to describe, see the following URL.
   * http://krrrr38.github.io/slick-doc-ja/v3.0.out/%E3%82%B9%E3%82%AD%E3%83%BC%E3%83%9E.html
   */
  class TriggerTable(tag: Tag) extends Table[Trigger](tag, "triggers") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val uuid = column[String]("uuid")
    val backlogIssuekey = column[String]("backlog_issuekey")
    val backlogStatus = column[String]("backlog_status")
    val circleciUsername = column[String]("circleci_username")
    val circleciRepository = column[String]("circleci_reposigory")
    val circleciPipeline = column[String]("circleci_pipeline")
    val circleciApikey = column[String]("circleci_apikey")
    val excuteAt = column[Option[Date]]("executed_at")
    val createAt = column[Date]("create_at")
    val deleteAt = column[Option[Date]]("delete_at")

    def * = (
      uuid,
      backlogIssuekey,
      backlogStatus,
      circleciUsername,
      circleciRepository,
      circleciPipeline,
      circleciApikey,
      createAt,
      excuteAt,
      deleteAt,
      id.?) <> (Trigger.tupled, Trigger.unapply)

  }
}

/**
 * @param uuid               Generated UUID
 * @param backlogIssuekey    Issueky in Backlog (example: ABC-1)
 * @param backlogStatu       status code in Backlog (example: 3)
 * @param circleciUsername   Username in CircleCI (example: AkiUnleash)
 * @param circleciRepository Reposigory in CircleCI (example: Repository)
 * @param circleciPipeline   Pipeline in CircleCI (example: main)
 * @param circleciApikey     Apikey in CircleCI (example : iexexkx...)
 * @param createAt           Date when the trigger data was created.
 * @param excuteAt           Date when the trigger data was exeuted.
 * @param deleteAt           Date when the trigger data was deleted.
 * @param id                 Automatic numbering
 */
case class Trigger(uuid: String,
                   backlogIssuekey: String,
                   backlogStatus: String,
                   circleciUsername: String,
                   circleciRepository: String,
                   circleciPipeline: String,
                   circleciApikey: String,
                   createAt: Date,
                   excuteAt: Option[Date] = None,
                   deleteAt: Option[Date] = None,
                   id: Option[Int] = None)


/**
 * @param triggers List of [[model.Trigger]]
 */
case class TriggerList(triggers: List[Trigger])

/**
 * @param backlogIssuekey    Issueky in Backlog (example: ABC-1)
 * @param backlogStatu       status code in Backlog (example: 3)
 * @param circleciUsername   Username in CircleCI (example: AkiUnleash)
 * @param circleciRepository Reposigory in CircleCI (example: Repository)
 * @param circleciPipeline   Pipeline in CircleCI (example: main)
 * @param circleciApikey     Apikey in CircleCI (example : iexexkx...)
 */
case class TriggerPost(backlogIssuekey: String,
                       backlogStatus: String,
                       circleciUsername: String,
                       circleciRepository: String,
                       circleciPipeline: String,
                       circleciApikey: String)
