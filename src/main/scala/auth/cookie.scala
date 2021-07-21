package auth

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{Created, Unauthorized}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.model.headers.HttpCookie

trait cookie {
  val cookieName = "jwt"

  def settingCookie(token: String) = {
    setCookie(HttpCookie(cookieName, value = token, httpOnly = true)) {
      complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,
          """{"message": "Success"}""")))
    }
  }

  def delCookie() = {
    deleteCookie(cookieName) {
      complete(HttpResponse(Created,
        entity = HttpEntity(ContentTypes.`application/json`,
          """{"message": "Success"}""")))
    }
  }
}
