package auth

import com.nulabinc.backlog4j._
import com.nulabinc.backlog4j.conf._

/** Processing the Backlog API */
trait Backlog {

  /** Verify that the spaceKey and apiKye are vaild.
   *
   * @param spaceKey spacekey in Backlog (example: akiunleash)
   * @param apiKey   apiKey in Backlog (example: eiox93ox...)
   */
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