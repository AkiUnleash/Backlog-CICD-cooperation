package auth

import com.nulabinc.backlog4j._
import com.nulabinc.backlog4j.conf._
import com.nulabinc.backlog4j.BacklogAPIError

trait backlog {
  def authentication(spaceKey: String, apiKey: String): Boolean = {
    val configure = new BacklogComConfigure(spaceKey).apiKey(apiKey)
    val backlog = new BacklogClientFactory(configure).newClient
    try {
      println(backlog.getProjects)
      true
    } catch {
      case e => false
    }
  }
}