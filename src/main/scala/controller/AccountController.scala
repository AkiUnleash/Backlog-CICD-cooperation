package controller

import model.{Account}
import scala.concurrent.Future

/** Controller for operationg account data */
trait AccountController extends AbstractController {

  import driver.api._

  protected val AccountTableQuery = TableQuery[AccountTable]

  /** Add account data to the database.
   *
   * @param account [[model.Account]]
   */
  def create(account: Account): Future[Int] = db.run {
    AccountTableAutoInc += account
  }

  /** Adding a record */
  def AccountTableAutoInc =
    AccountTableQuery returning AccountTableQuery.map(_.id)

  /** Get account data that matches the spacekey of Backlog.
   *
   * @param backlogSpacekey Spacekey of Backlog
   */
  def getByUser(backlogSpacekey: String): Future[Option[Account]] = db.run {
    AccountTableQuery.filter(_.backlogSpacekey === backlogSpacekey).result.headOption
  }
}