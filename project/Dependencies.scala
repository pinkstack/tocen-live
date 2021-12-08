import sbt._

object Dependencies {

  type Version = String

  object Versions {
    val ScalaTest: Version = "3.2.10"
    val Cats: Version = "2.7.0"
    val CatsEffect: Version = "3.3.0"
    val Circe: Version = "0.14.0"
    val CirceJWT: Version = "9.0.2"
    val LogbackClassic: Version = "1.2.7"
    val LogbackClassicJSON: Version = "0.1.5"
    val ScalaLogging: Version = "3.9.4"
    val Akka: Version = "2.6.17"
    val AkkaHttp: Version = "10.2.7"
    val AkkaCors: Version = "1.1.2"
    val Scalacache: Version = "1.0.0-M6"
    val Slick: Version = "3.3.3"
    val SlickPG: Version = "0.19.7"
    val fs2: Version = "3.2.3"
    val sttp: Version = "3.3.18"
    val log4cats: Version = "2.1.1"
    val CatsEffectTesting: Version = "1.4.0"
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

  val akka = Seq(
    // Classic
    "com.typesafe.akka" %% "akka-actor" % Versions.Akka,
    "com.typesafe.akka" %% "akka-testkit" % Versions.Akka % Test,

    // Typed
    "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.Akka % Test,

    // Http
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
    "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp,
    // "com.typesafe.akka" %% "akka-http-xml" % Versions.AkkaHttp

    // CORS
    "ch.megard" %% "akka-http-cors" % Versions.AkkaCors
  )

  val scalacache: Seq[ModuleID] = Seq(
    "com.github.cb372" %% "scalacache-core",
    "com.github.cb372" %% "scalacache-redis",
    // "com.github.cb372" %% "scalacache-cats-effect",
  ).map(_ % Versions.Scalacache)

  val googleLibs: Seq[ModuleID] = Seq(
    "com.google.cloud" % "google-cloud-texttospeech" % "2.0.6",
    "com.google.cloud" % "google-cloud-speech" % "2.1.2",
    "com.google.cloud" % "google-cloud-storage" % "2.2.1"
  ) ++ Seq(
    "com.google.cloud.sql" % "postgres-socket-factory",
    "com.google.cloud.sql" % "jdbc-socket-factory-parent"
  ).map(_ % "1.4.0") ++ Seq(
    "com.google.cloud" % "google-cloud-logging" % "3.4.0",
    "com.google.cloud" % "google-cloud-logging-logback" % "0.122.3-alpha"
  )

  val slick: Seq[ModuleID] = Seq(
    "com.typesafe.slick" %% "slick",
    "com.typesafe.slick" %% "slick-hikaricp",
    // "com.typesafe.slick" %% "slick-codegen",
  ).map(_ % Versions.Slick) ++ Seq(
    "org.postgresql" % "postgresql" % "42.3.1",
    "org.flywaydb" % "flyway-core" % "8.1.0",
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "3.0.3",
  ) ++ Seq(
    "com.github.tminglei" %% "slick-pg",
    "com.github.tminglei" %% "slick-pg_circe-json"
  ).map(_ % Versions.SlickPG)

  val myResolvers: Seq[MavenRepository] = Seq(
    "Artima Maven Repository" at "https://repo.artima.com/releases"
  )
}
