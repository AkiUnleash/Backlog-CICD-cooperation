package controller

import model.{AccountModel, Account}
import scala.concurrent.Future

trait accountController extends abstractController {
  import driver.api._

  protected val AccountTableQuery = TableQuery[AccountTable]

  def create(account: Account): Future[Int] = db.run {
    AccountTableAutoInc += account
  }

  def getByUser(backlogSpacekey: String): Future[Option[Account]] = db.run {
    AccountTableQuery.filter(_.backlogSpacekey === backlogSpacekey).result.headOption
  }

  def AccountTableAutoInc =
    AccountTableQuery returning AccountTableQuery.map(_.id)
}