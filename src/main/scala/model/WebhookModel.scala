package model

import spray.json.DefaultJsonProtocol

/** Apply JSON received by Webhook to Case Class */
trait WebhookModel extends DefaultJsonProtocol {
  implicit lazy val SendFormat = jsonFormat1(Send)
  implicit lazy val StatusFormat = jsonFormat2(Status)
  implicit lazy val PriorityFormat = jsonFormat2(Priority)
  implicit lazy val IssueTypeFormat = jsonFormat5(IssueType)
  implicit lazy val ContentFormat = jsonFormat8(Content)
  implicit lazy val ProjectFormat = jsonFormat9(Project)
  implicit lazy val WebhookFormat = jsonFormat4(WebhookData)
}

/**
 * @param id      Automatic numbering
 * @param project [[model.Project]]
 * @param type    number
 * @param content [[model.Content]]
 */
case class WebhookData(
                        id: Int,
                        project: Project,
                        `type`: Int,
                        content: Content)

/**
 * @param id                                Automatic numbering
 * @param projectKey                        projectKey
 * @param name                              name
 * @param chartEnabled                      chartEnabled
 * @param subtaskingEnabled                 subtaskingEnabled
 * @param projectLeaderCanEditProjectLeader projectLeaderCanEditProjectLeader
 * @param useWikiTreeView                   useWikiTreeView
 * @param textFormattingRule                textFormattingRule
 * @param archived                          archived
 */
case class Project(
                    id: Int,
                    projectKey: String,
                    name: String,
                    chartEnabled: Boolean,
                    subtaskingEnabled: Boolean,
                    projectLeaderCanEditProjectLeader: Boolean,
                    useWikiTreeView: Boolean,
                    textFormattingRule: String,
                    archived: Boolean)

/**
 * @param id          Automatic numbering
 * @param key_id      key_id
 * @param summary     summary
 * @param description description
 * @param issueType   issueType
 * @param resolution  resolution
 * @param priority    priority
 * @param status      status
 */
case class Content(
                    id: Int,
                    key_id: Int,
                    summary: String,
                    description: String,
                    issueType: IssueType,
                    resolution: Option[Int],
                    priority: Priority,
                    status: Status)

/**
 * @param id           Automatic numbering
 * @param projectId    projectId
 * @param name         name
 * @param color        color
 * @param displayOrder displayOrder
 */
case class IssueType(
                      id: Int,
                      projectId: Int,
                      name: String,
                      color: String,
                      displayOrder: Int)

/**
 * @param id   Automatic numbering
 * @param name name
 */
case class Priority(
                     id: Int,
                     name: String)

/**
 * @param id   Automatic numbering
 * @param name name
 */
case class Status(
                   id: Int,
                   name: String)

/**
 * @param id   Automatic numbering
 * @param name name
 */
case class Send(
                 branch: String)