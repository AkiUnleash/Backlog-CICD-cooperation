package http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives.complete
import spray.json._

/** HTTP response processing */
trait Response extends DefaultJsonProtocol {

  implicit lazy val ResponseParameterFormat = jsonFormat1(ResponseParameter)

  /** OK response (200)
   *
   * @param message Display it in the response body
   */
  def okResponse(message: String = "Success") = {
    val requestJson = ResponseParameter(message)
    complete(
      HttpResponse(
        entity = HttpEntity(
          ContentTypes.`application/json`,
          requestJson.toJson.toString())
      )
    )
  }

  /** Bad response (400)
   *
   * @param message Display it in the response body
   */
  def badResponse(message: String) = {
    val requestJson = ResponseParameter(message = message)
    complete(
      HttpResponse(
        BadRequest,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          requestJson.toJson.toString())
      )
    )
  }

  /** Unautorize response (401)
   *
   * @param message Display it in the response body
   */
  def unauthorizedResponse(message: String) = {
    val requestJson = ResponseParameter(message = message)
    complete(
      HttpResponse(
        Unauthorized,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          requestJson.toJson.toString())
      )
    )
  }
}

/**
 * @param message Display it in the response body
 */
case class ResponseParameter(message: String)
