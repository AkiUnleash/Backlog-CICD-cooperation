package router

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.protobufv3.internal.StringValue
import controller.{accountController, triggerController}
import org.springframework.scheduling.TriggerContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import spray.json._
import DefaultJsonProtocol._

import java.time.Clock
import java.util.UUID.randomUUID
import java.sql.Date
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import model.{Account, AccountLogin, Trigger, TriggerPost, WebhookData, Send}
import auth.{backlog, cookie, jwt}
import http.request

import scala.util.{Failure, Success}


trait routes extends SprayJsonSupport
  with triggerController
  with accountController
  with backlog
  with cookie
  with jwt
{

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val routes = {
      pathPrefix("webhook") {
        path(".+".r) { uuid => {
          post {
            entity(as[WebhookData]) { d =>

              val triggerData = getByTrigger(uuid,
                d.project.projectKey + "-" + d.content.key_id,
                d.content.status.id.toString)
              val data = Await.result(triggerData, 1.second)

              data match {
                case Some(uuid) =>
                  val SendData = Send(branch = data.get.circleciPipeline)
                  val jsonData = SendData.toJson

                  request.cicdRun(jsonData,
                    data.get.circleciUsername,
                    data.get.circleciRepository,
                    data.get.circleciApikey)

                  exacuteTrigger(data.get.id)

                  complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,
                    """{"message": "Success"}""")))
                case None =>
                  complete(HttpResponse(BadRequest,
                    entity = HttpEntity(ContentTypes.`application/json`,
                      """{"message" : "No matching data"}""")))
              }
            }
          }
        }
        }
      } ~
      pathPrefix("db") {
        // Register Account
        pathPrefix("issues") {
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
                        complete(HttpResponse(BadRequest,
                          entity = HttpEntity(ContentTypes.`application/json`,
                            """{"message" : "Data already registered"}""")))
                      case None =>
                        val currentDate = new Date(System.currentTimeMillis())
                        val triggerPost = Trigger(
                          jwtDecode(nameCookie.value),
                          trigger.backlogIssuekey,
                          trigger.backlogStatus,
                          trigger.circleciUsername,
                          trigger.circleciRepository,
                          trigger.circleciPipeline,
                          trigger.circleciApikey,
                          currentDate,
                          null,
                          null
                        )
                        complete {
                          create(triggerPost).map { result =>
                            HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,
                                """{"message" : "Success"}"""))
                          }
                        }
                    }
                  }
                case None =>
                  complete(HttpResponse(Unauthorized,
                    entity = HttpEntity(ContentTypes.`application/json`,
                      """{"message" : "Backlog authentication error"}""")))
              }
            } ~
              get {
                optionalCookie(cookieName) {
                  case Some(nameCookie) =>
                    complete(getByTriggerList(jwtDecode(nameCookie.value)))
                  case None =>
                    complete(HttpResponse(Unauthorized,
                      entity = HttpEntity(ContentTypes.`application/json`,
                        """{"message" : "Backlog authentication error"}""")))
                }
              }
          } ~
            path(IntNumber) { id => {
              delete {
                optionalCookie(cookieName) {
                  case Some(nameCookie) =>
                    complete {
                      println(nameCookie.value, id)
                      deleteTrigger(jwtDecode(nameCookie.value), id).map { result =>
                        HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,
                          """{"message" : "Success"}"""))
                      }
                    }
                  case None =>
                    complete(HttpResponse(Unauthorized,
                      entity = HttpEntity(ContentTypes.`application/json`,
                        """{"message" : "Backlog authentication error"}""")))
                }
              }
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
              case List(false) =>
                complete(HttpResponse(Unauthorized,
                  entity = HttpEntity(ContentTypes.`application/json`,
                  """{"message" : "Backlog authentication error"}""")))

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
                    val accountPost = Account(uuid, accounts.backlogSpacekey, currentDate, null)
                    create(accountPost)
                    val token = jwtEncode(uuid)
                    settingCookie(token)

                }
            }
          }
        }
      } ~
      path("logout") {
        post {
          delCookie()
        }
      }
  }
}