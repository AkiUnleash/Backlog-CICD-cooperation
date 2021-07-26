import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.util.ByteString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import router.Routes
import com.typesafe.config.ConfigFactory

class ServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with Routes {

  "/login(POST)" when {
    "Nomal paramater" should {
      "OK Response" in {

        val config = ConfigFactory.load()
        val spaceKey = config.getString("test.nomalSpacekey")
        val apiKey = config.getString("test.nomalApikey")

        val jsonRequest = ByteString(
          s"""
             |{
             |    "backlogSpacekey":"$spaceKey",
             |    "backlogApikey":"$apiKey"
             |}
        """.stripMargin)

        val postRequest = HttpRequest(
          HttpMethods.POST,
          uri = "/login",
          entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

        postRequest ~> routes ~> check {
          status should ===(StatusCodes.OK)
        }
      }

      "Non-nomal paramater" should {
        "Unauthorized Response" in {

          val config = ConfigFactory.load()
          val spaceKey = config.getString("test.nonNomalSpacekey")
          val apiKey = config.getString("test.nonNomalApikey")

          val jsonRequest = ByteString(
            s"""
               |{
               |    "backlogSpacekey":"$spaceKey",
               |    "backlogApikey":"$apiKey"
               |}
        """.stripMargin)

          val postRequest = HttpRequest(
            HttpMethods.POST,
            uri = "/login",
            entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

          postRequest ~> routes ~> check {
            status should ===(StatusCodes.Unauthorized)
          }

        }
      }
    }
  }
}