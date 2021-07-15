package model

import spray.json.DefaultJsonProtocol

trait WebhookModel extends DefaultJsonProtocol {
  implicit lazy val StatusFormat = jsonFormat2(Status)
  implicit lazy val PriorityFormat = jsonFormat2(Priority)
  implicit lazy val IssueTypeFormat = jsonFormat5(IssueType)
  implicit lazy val ContentFormat = jsonFormat8(Content)
  implicit lazy val ProjectFormat = jsonFormat9(Project)
  implicit lazy val WebhookFormat = jsonFormat4(WebhookData)
}

case class WebhookData(
                        id: Int,
                        project: Project,
                        `type`: Int,
                        content: Content)

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

case class Content(
                    id: Int,
                    key_id: Int,
                    summary: String,
                    description: String,
                    issueType: IssueType,
                    resolution: Option[Int],
                    priority: Priority,
                    status: Status)

case class IssueType(
                      id: Int,
                      projectId: Int,
                      name: String,
                      color: String,
                      displayOrder: Int)

case class Priority(
                     id: Int,
                     name: String)

case class Status(
                   id: Int,
                   name: String)
