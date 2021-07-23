package auth

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{Created}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.HttpCookie

/** Processing on Cookie */
trait Cookie {
  val cookieName = "jwt"

  /** Storing cookies on the client.
   *
   * @param data Token data to be stroed.
   */
  def settingCookie(token: String) = {
    setCookie(HttpCookie(cookieName, value = token, httpOnly = true)) {
      complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,
          """{"message": "Success"}""")))
    }
  }

  /** Delete the client's cookies. */
  def delCookie() = {
    deleteCookie(cookieName) {
      complete(HttpResponse(Created,
        entity = HttpEntity(ContentTypes.`application/json`,
          """{"message": "Success"}""")))
    }
  }
}
