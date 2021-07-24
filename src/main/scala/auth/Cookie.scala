package auth

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{Created}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.HttpCookie
import http.Response


/** Processing on Cookie */
trait Cookie extends Response {
  val cookieName = "jwt"

  /** Storing cookies on the client.
   *
   * @param data Token data to be stroed.
   */
  def settingCookie(token: String) = {
    setCookie(HttpCookie(cookieName, value = token, httpOnly = true)) {
      okResponse()
    }
  }

  /** Delete the client's cookies. */
  def delCookie() = {
    deleteCookie(cookieName) {
      okResponse()
    }
  }
}
