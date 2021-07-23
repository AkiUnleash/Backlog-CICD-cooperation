package http

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import spray.json._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives.complete
import akka.util.ByteString
import scala.concurrent.Future
import scala.util.{Failure, Success}

/** Factory for [[http.Request]] instances. */
object Request {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext = system.executionContext

  /** Send Request to CI/CD Tool to execute.
   *
   * @param jsonData   Json describing the execution branch.
   * @param username   CircleCI(Github) user name.
   * @param repository CircleCI(Github) repository.
   */
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
    ))

    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => sys.error("something wrong")
      }
    complete("end")
  }
}
