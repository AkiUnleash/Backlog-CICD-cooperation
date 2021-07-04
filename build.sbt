name := "Backlog-CICD-cooperation"

version := "0.1"

scalaVersion := "3.0.0"

libraryDependencies ++= {
  val backlog4jVersion = "2.4.4"
  Seq(
    "com.nulab-inc" % "backlog4j" % backlog4jVersion,
    "com.typesafe" % "config" % "1.4.1"
  )
}