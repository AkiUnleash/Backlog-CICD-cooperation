import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.util.ByteString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import router.Routes

class ServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with Routes {

  "/login(POST)" when {
    "Nomal paramater" should {
      "OK Response" in {

        val spaceKey = "akiunleash"
        val apiKey = "b8IiVPfLHVUprTDzlUEUnKw6jDusYBPqdHGsq9mwjwQ3nzQVHYmZwKP4kIgjhBhe"

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

          val spaceKey = "none!!!"
          val apiKey = "b8IiVPfLHVUprTDzlUEUnKw6jDusYBPqdHGsq9mwjwQ3nzQVHYmZwKP4kIgjhBhe"

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