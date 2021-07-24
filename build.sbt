import com.typesafe.sbt.packager.docker.ExecCmd
import sbt.Keys.libraryDependencies

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.akiunleash.backlog",
      scalaVersion := "2.13.6",
      version := "0.1"
    )),
    name := "backlog-cicd-cooperation",
    libraryDependencies ++= {
      lazy val AkkaVersion = "2.6.8"
      lazy val AkkaHttpVersion = "10.2.4"
      lazy val SlickVersion = "3.3.3"
      lazy val MysqlVersion = "8.0.25"
      lazy val springVersion = "5.3.10.RELEASE"
      lazy val backlog4jVersion = "2.4.4"
      lazy val jwtVersion = "8.0.2"
      lazy val logbackVersion = "1.3.0-alpha5"
      lazy val configVersion = "1.4.1"
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
        "com.nulab-inc" % "backlog4j" % backlog4jVersion,
        "com.github.jwt-scala" %% "jwt-core" % jwtVersion,
        "ch.qos.logback" % "logback-classic" % logbackVersion,
        "com.typesafe" % "config" % configVersion
      )
    },
    defaultLinuxInstallLocation in Docker := "/opt/docker",
    executableScriptName := "app",
    dockerBaseImage := "openjdk:11",
    dockerUpdateLatest := true,
    mainClass in(Compile, bashScriptDefines) := Some("Server"),
    packageName in Docker := name.value,
    dockerCommands := dockerCommands.value.filter {
      case ExecCmd("CMD", _*) => false
      case _ => true
    }.map {
      case ExecCmd("ENTRYPOINT", args@_*) => ExecCmd("CMD", args: _*)
      case other => other
    }
  )
  .enablePlugins(AshScriptPlugin)



