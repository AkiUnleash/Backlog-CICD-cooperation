import com.nulabinc.backlog4j.*
import com.nulabinc.backlog4j.conf.*
import com.nulabinc.backlog4j.api
import com.nulabinc.backlog4j.api.option.GetIssuesParams
import scala.collection.JavaConverters._
import collection.mutable._
import com.typesafe.config.ConfigFactory

object Sample extends App {

  val config = ConfigFactory.load()
  val spaceKey = config.getString("spaceKey")
  val apiKey = config.getString("apiKey")
  val projectId = config.getString("projectId")

  val configure = new BacklogComConfigure(spaceKey).apiKey(apiKey)
  val backlog = new BacklogClientFactory(configure).newClient
  val project = backlog.getProject(projectId)


    val projectIds = List(178990).asJava
    val p =  new GetIssuesParams(projectIds)
    val Issues =  backlog.getIssues(p)
    println(Issues)
}
