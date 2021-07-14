package router

import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import controller.{accountController, triggerController}
import org.springframework.scheduling.TriggerContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import java.time.Clock
import java.util.UUID.randomUUID
import java.sql.Date
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import model.{Account, AccountLogin, Trigger, TriggerPost}
import auth.{backlog, cookie, jwt}

trait routes extends SprayJsonSupport
  with triggerController with accountController with backlog with cookie with jwt {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val routes = {
    pathPrefix("db") {
      // Register Account
      path("issues") {
        pathEnd {
          post {
            optionalCookie(cookieName) {
              case Some(nameCookie) =>
                entity(as[TriggerPost]) { trigger =>

                  val triggerData = getByTrigger(jwtDecode(nameCookie.value),
                    trigger.backlogIssuekey,
                    trigger.backlogStatus)
                  val data = Await.result(triggerData, 1.second)

                  data match {
                    case Some(uuid) =>
                      complete("already data")
                    case None =>
                      val currentDate = new Date(System.currentTimeMillis())
                      val triggerPost = Trigger(
                        jwtDecode(nameCookie.value),
                        trigger.backlogIssuekey,
                        trigger.backlogStatus,
                        trigger.circleciPipeline,
                        trigger.circleciApikey,
                        currentDate,
                        null,
                        null
                      )
                      complete {
                        create(triggerPost).map { result => HttpResponse(entity = "Account has been saved successfully") }
                      }
                  }
                }
              case None => complete("None Cookie.")
            }
          } ~
            get {
              optionalCookie(cookieName) {
                case Some(nameCookie) =>
                  complete(getByTriggerList(jwtDecode(nameCookie.value)))
                case None => complete("None Cookie.")
              }
            }
        }
      } ~
        path(IntNumber) { id => {
          delete {

          }
        }
        }
    } ~
      path("login") {
        post {
          entity(as[AccountLogin]) { accounts =>

            val authenticationFlg = authentication(accounts.backlogSpacekey, accounts.backlogApikey)
            val currentDate = new Date(System.currentTimeMillis())

            List(authenticationFlg) match {
              case List(false) => complete(HttpResponse(entity = "Backlog authentication error."))
              case List(true) =>
                val accountData = getByUser(accounts.backlogSpacekey)
                val data = Await.result(accountData, 1.second)

                data match {
                  case Some(uuid) =>
                    val uuid = data.get.uuid
                    val token = jwtEncode(uuid)
                    settingCookie(token)
                  case None =>
                    val uuid = randomUUID.toString()
                    val accountPost = Account(uuid , accounts.backlogSpacekey, currentDate, null )
                    create(accountPost)
                    val token = jwtEncode(uuid)
                    settingCookie(token)
                }
            }
          }
        }
      } ~
        path("logout") {
          post { delCookie() }
        }
  }
}