package http

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.common.JsonEntityStreamingSupport
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives.complete
import akka.util.ByteString
import org.springframework.security.core.Authentication

import scala.concurrent.Future
import scala.util.{Failure, Success}
import model.Send

object Request {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext = system.executionContext

  def cicdRun(jsonData: JsValue, username: String, repository: String, apikey: String): akka.http.scaladsl.server.Route = {

    val url = s"https://circleci.com/api/v2/project/gh/$username/$repository/pipeline"

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      POST,
      uri = url,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        data = ByteString(jsonData.toString())
      )
    ).withHeaders(
      Authorization(BasicHttpCredentials(apikey, ""))
    )

    )

    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => sys.error("something wrong")
      }
    complete("end")
  }
}
