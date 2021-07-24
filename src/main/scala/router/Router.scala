package router

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import controller.{AccountController, TriggerController}
import spray.json._
import java.util.UUID.randomUUID
import java.sql.Date
import scala.concurrent.Await
import scala.concurrent.duration._
import model.{Account, AccountLogin, Trigger, TriggerPost, WebhookData, Send}
import auth.{Backlog, Cookie, JwtAuthentication}
import http.{Request, Response}

/** Specifying the route */
trait Routes extends SprayJsonSupport
  with TriggerController
  with AccountController
  with Backlog
  with Cookie
  with JwtAuthentication
  with Response {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val routes = {
    pathPrefix("webhook") {
      path(".+".r) { uuid => {
        post {
          entity(as[WebhookData]) { webhookdata =>
            webhookHandler(uuid, webhookdata)
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
              issueCreateHandler
            } ~
              get {
                issueGetHandler
              }
          } ~
            path(IntNumber) { id => {
              delete {
                issueDeleteHandler(id)
              }
            }
            }
        }
      } ~
      path("login") {
        post {
          entity(as[AccountLogin]) { accounts =>
            loginHandler(accounts)
          }
        }
      } ~
      path("logout") {
        post {
          delCookie()
        }
      }
  }

  /** Processing when a request is received in /webhook/:uuid (POST)
   *
   * @param uuid        Generated UUID
   * @param webhookdata Request data received
   */
  private def webhookHandler(uuid: String, webhookdata: WebhookData) = {

    val triggerData = getByTrigger(uuid,
      webhookdata.project.projectKey + "-" + webhookdata.content.key_id,
      webhookdata.content.status.id.toString)

    val data = Await.result(triggerData, 1.second)

    data match {
      case Some(uuid) =>
        val SendData = Send(branch = data.get.circleciPipeline)
        val jsonData = SendData.toJson

        Request.cicdRun(jsonData,
          data.get.circleciUsername,
          data.get.circleciRepository,
          data.get.circleciApikey)

        exacuteTrigger(data.get.id)

        okResponse()

      case None =>
        badResponse("No matching data")
    }
  }

  /** Processing when a request is received in /db/issues (POST) */
  private def issueCreateHandler = {

    optionalCookie(cookieName) {
      case Some(nameCookie) =>
        entity(as[TriggerPost]) { trigger =>

          val triggerData = getByTrigger(jwtDecode(nameCookie.value),
            trigger.backlogIssuekey,
            trigger.backlogStatus)

          val data = Await.result(triggerData, 1.second)

          data match {
            case Some(uuid) =>
              badResponse("Data already registered")
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

              create(triggerPost)

              okResponse()
          }
        }

      case None =>
        unauthorizedResponse("Backlog authentication error")
    }
  }

  /** Processing when a request is received in /db/issues (GET) */
  private def issueGetHandler = {
    optionalCookie(cookieName) {
      case Some(nameCookie) =>
        complete(getByTriggerList(jwtDecode(nameCookie.value)))
      case None =>
        unauthorizedResponse("Backlog authentication error")
    }
  }

  /** Processing when a request is received in /db/issues (DELETE)
   *
   * @param id Specify the ID of the trigger data.
   */
  private def issueDeleteHandler(id: Int) = {
    optionalCookie(cookieName) {
      case Some(nameCookie) =>
        deleteTrigger(jwtDecode(nameCookie.value), id)
        okResponse()
      case None =>
        unauthorizedResponse("Backlog authentication error")
    }
  }

  /** Processing when a request is received in /login (POST)
   *
   * @param account Specify the ID of the trigger data.
   */
  private def loginHandler(account: AccountLogin) = {

    val authenticationFlg = authentication(account.backlogSpacekey, account.backlogApikey)
    val currentDate = new Date(System.currentTimeMillis())

    authenticationFlg match {
      case false =>
        unauthorizedResponse("Backlog authentication error")

      case true =>
        val accountData = getByUser(account.backlogSpacekey)
        val data = Await.result(accountData, 1.second)
        data match {
          case Some(uuid) =>
            val uuid = data.get.uuid
            val token = jwtEncode(uuid)
            settingCookie(token)

          case None =>
            val uuid = randomUUID.toString()
            val accountPost = Account(uuid, account.backlogSpacekey, currentDate, null)
            create(accountPost)
            val token = jwtEncode(uuid)
            settingCookie(token)
        }
    }
  }
}