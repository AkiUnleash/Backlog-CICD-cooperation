package router

import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import controller.triggerController
import model.{Trigger, TriggerModel, TriggerPost}
import org.springframework.scheduling.TriggerContext

import java.time.Clock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import java.util.UUID.randomUUID
import java.sql.Date
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

trait TriggerRoutes extends SprayJsonSupport {
  this: triggerController =>

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val routes = {
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
    }
  }


}