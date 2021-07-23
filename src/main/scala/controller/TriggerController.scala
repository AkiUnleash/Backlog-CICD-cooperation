package controller

import model.{Trigger}
import java.sql.Date
import scala.concurrent.Future

/** Controller for operationg trigger data */
trait TriggerController extends AbstractController {

  import driver.api._

  protected val TriggerTableQuery = TableQuery[TriggerTable]

  /** Add trigger data to the database.
   *
   * @param trigger [[model.Trigger]]
   */
  def create(trigger: Trigger): Future[Int] = db.run {
    TriggerTableAutoInc += trigger
  }

  /** Adding a record */
  def TriggerTableAutoInc =
    TriggerTableQuery returning TriggerTableQuery.map(_.id)

  /** Get trigger data that matches the condition.
   *
   * @param uuid            Logged-in UUID
   * @param backlogIssuekey Issuekey of Backlog (example: ABC-1)
   * @param backlogStatus   Status of Backlog (example: 3)
   */
  def getByTrigger(uuid: String,
                   backlogIssuekey: String,
                   backlogStatus: String): Future[Option[Trigger]] = db.run {
    TriggerTableQuery
      .filter(_.uuid === uuid)
      .filter(_.backlogIssuekey === backlogIssuekey)
      .filter(_.backlogStatus === backlogStatus)
      .filter(_.excuteAt.isEmpty)
      .result.headOption
  }

  /** Get trigger data that matches the uuid.
   *
   * @param uuid Logged-in UUID
   */
  def getByTriggerList(uuid: String): Future[List[Trigger]] = db.run {
    TriggerTableQuery.filter(_.uuid === uuid).to[List].result
  }

  /** Deletes the trigger data whose UUID and ID match.
   *
   * @param uuid Logged-in UUID
   * @param uuid Specifying a number
   */
  def deleteTrigger(uuid: String, id: Int): Future[Int] = db.run {
    val currentDate = new Date(System.currentTimeMillis())
    val q = for {l <- TriggerTableQuery if l.uuid === uuid && l.id === id} yield l.deleteAt
    q.update(Option(currentDate))
  }

  /** Record the execution data in the trigger data.
   *
   * @param uuid Specifying a number
   */
  def exacuteTrigger(id: Option[Int]): Future[Int] = db.run {
    val currentDate = new Date(System.currentTimeMillis())
    val q = for {l <- TriggerTableQuery if l.id === id} yield l.excuteAt
    q.update(Option(currentDate))
  }

}