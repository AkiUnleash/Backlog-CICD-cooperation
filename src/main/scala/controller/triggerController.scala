package controller

import model.{TriggerModel, Trigger}
import java.sql.Date
import scala.concurrent.Future

trait triggerController extends abstractController {

  import driver.api._

  protected val TriggerTableQuery = TableQuery[TriggerTable]

  def create(trigger: Trigger): Future[Int] = db.run {
    TriggerTableAutoInc += trigger
  }

  def getByTrigger(uuid: String,
                   backlogIssuekey: String,
                   backlogStatus: String): Future[Option[Trigger]] = db.run {
    TriggerTableQuery
      .filter(_.uuid === uuid)
      .filter(_.backlogIssuekey === backlogIssuekey )
      .filter(_.backlogStatus === backlogStatus )
      .filter(_.excuteAt.isEmpty)
      .result.headOption
  }

  def getByTriggerList(uuid: String): Future[List[Trigger]] = db.run {
    TriggerTableQuery.filter(_.uuid === uuid).to[List].result
  }

  def deleteTrigger(uuid: String, id: Int): Future[Int] = db.run {
    val currentDate = new Date(System.currentTimeMillis())
    val q = for { l <- TriggerTableQuery if l.uuid === uuid && l.id === id } yield l.deleteAt
    q.update(Option(currentDate))
  }

  def exacuteTrigger(id: Option[Int]): Future[Int] = db.run {
    val currentDate = new Date(System.currentTimeMillis())
    val q = for { l <- TriggerTableQuery if l.id === id } yield l.excuteAt
    q.update(Option(currentDate))
  }

  def TriggerTableAutoInc =
    TriggerTableQuery returning TriggerTableQuery.map(_.id)
}