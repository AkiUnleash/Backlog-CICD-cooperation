import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import router.Routes

/**
 * Factory for [[Server]] instances.
 */
object Server extends App with Routes {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext = system.executionContext

  ddl.onComplete { _ =>
      val port = sys.env.getOrElse("PORT", "8000").toInt
      Http().newServerAt("0.0.0.0", port).bind(routes)
      println("----- start -----")
  }
}