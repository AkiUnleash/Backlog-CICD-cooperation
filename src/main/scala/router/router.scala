package router

import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import controller.{triggerController, accountController}
import model.{Trigger, TriggerModel, TriggerPost, AccountLogin, Account}
import org.springframework.scheduling.TriggerContext

import java.time.Clock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import java.util.UUID.randomUUID
import java.sql.Date
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

trait routes extends SprayJsonSupport with triggerController with accountController {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val routes = {
    path("aaa"){ complete ("")}
    pathPrefix("db") {
      // Register Account
      path("issues") {
        post {
            entity(as[TriggerPost]) { trigger =>
            val currentDate = new Date(System.currentTimeMillis())
            val triggerPost = Trigger(
              trigger.backlogSpacekey,
              trigger.backlogApikey,
              trigger.backlogIssuekey,
              trigger.backlogStatus,
              trigger.circleciPipeline,
              trigger.circleciApikey,
              currentDate,
              null,
              null
            )
            complete{
              create(triggerPost).map { result => HttpResponse(entity = "Account has been saved successfully") }
            }
          }
        }
      }
    } ~
      path("login") {
        post {
          entity(as[AccountLogin]) { accounts =>
            val currentDate = new Date(System.currentTimeMillis())
            val accountPost = Account(
              randomUUID.toString(),
              accounts.backlogSpacekey,
              currentDate,
              null
            )
            complete {
              create(accountPost).map { result => HttpResponse(entity = "Account has been saved successfully") }
            }
          }
        }
      }
  }
}