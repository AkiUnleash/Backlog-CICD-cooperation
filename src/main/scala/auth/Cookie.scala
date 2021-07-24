package auth

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.HttpCookie
import http.Response
import com.typesafe.config.ConfigFactory

/** Processing on Cookie */
trait Cookie extends Response {
  private val config = ConfigFactory.load()
  val cookieName = config.getString("auth.cookieName")

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
