import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import controller.{triggerController, accountController}
import router.routes
import scala.io.StdIn


object Server extends App with routes {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext = system.executionContext

  ddl.onComplete {
    _ =>
      val port = sys.env.getOrElse("PORT", "8000").toInt
      val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(routes)

      println("----- start -----")

  }
}