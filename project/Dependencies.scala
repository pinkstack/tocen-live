import sbt._

object Dependencies {

  type Version = String

  object Versions {
    val ScalaTest: Version = "3.2.10"
    val Cats: Version = "2.7.0"
    val CatsEffect: Version = "3.3.0"
    val Circe: Version = "0.14.0"
    val LogbackClassic: Version = "1.2.7"
    val LogbackClassicJSON: Version = "0.1.5"
    val ScalaLogging: Version = "3.9.4"
    val fs2: Version = "3.2.3"
    val sttp: Version = "3.3.18"
    val log4cats: Version = "2.1.1"
    val CatsEffectTesting: Version = "1.4.0"
    val http4s: Version = "1.0.0-M30"
  }

  val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats,
    "org.typelevel" %% "cats-effect" % Versions.CatsEffect
  ) ++ Seq(
    "org.typelevel" %% "log4cats-core",
    "org.typelevel" %% "log4cats-slf4j"
  ).map(_ % Versions.log4cats)

  val sttp: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client3" %% "core",
    "com.softwaremill.sttp.client3" %% "circe",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats",
    "com.softwaremill.sttp.client3" %% "slf4j-backend"
  ).map(_ % Versions.sttp)

  val configurationLibs: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.1"
  )

  val fs2: Seq[ModuleID] = Seq(
    "co.fs2" %% "fs2-core",
    "co.fs2" %% "fs2-io",
    "co.fs2" %% "fs2-reactive-streams"
  ).map(_ % Versions.fs2)

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    // "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-fs2",
  ).map(_ % Versions.Circe)

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.LogbackClassic,
    "ch.qos.logback.contrib" % "logback-json-classic" % Versions.LogbackClassicJSON,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging,
  )

  val testingLibs: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % Versions.ScalaTest,
    "org.scalatest" %% "scalatest" % Versions.ScalaTest % Test,

    "org.scalatest" %% "scalatest-flatspec" % Versions.ScalaTest % Test,

    "org.typelevel" %% "cats-effect-testkit" % Versions.CatsEffect % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % Versions.CatsEffectTesting % Test
  )

  val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % Versions.http4s)

  val myResolvers: Seq[MavenRepository] = Seq(
    "Artima Maven Repository" at "https://repo.artima.com/releases",
    Resolver.sonatypeRepo("snapshots")
  )
}
