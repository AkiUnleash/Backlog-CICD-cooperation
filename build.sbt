name := "Backlog-CICD-cooperation"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= {
  lazy val AkkaVersion = "2.6.8"
  lazy val AkkaHttpVersion = "10.2.4"
  lazy val SlickVersion = "3.3.3"
  lazy val MysqlVersion = "8.0.25"
  lazy val springVersion = "5.3.10.RELEASE"
  Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.slick" %% "slick" % SlickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
    "mysql" % "mysql-connector-java" % MysqlVersion,
    "org.springframework.security" % "spring-security-web" % springVersion,
    "com.github.jwt-scala" %% "jwt-core" % "8.0.2",
  )
}