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
      // 内部処理とPathの結合
      val bindingFuture = Http().newServerAt("localhost", 8000).bind(routes)

      println(s"Server online at http://localhost:8000/\nPress RETURN to stop...")
      StdIn.readLine() // let it run until user presses return
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
  }
}