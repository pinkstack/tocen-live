import sbt._
import Keys._
import Dependencies._
import com.typesafe.sbt.packager.docker.Cmd

import scala.sys.process._

addCommandAlias("deployCurrent", ";compile;docker:publish;deployWithAnsible")

ThisBuild / scalaVersion := "2.13.7"
ThisBuild / publishArtifact := false
ThisBuild / organization := "com.pinkstack.tocenlive"
ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-unchecked",
  "-explaintypes", // Explain type errors in more detail.
  "-language:implicitConversions",
  "-language:existentials",
  "-language:dynamics",
  "-language:existentials",                    // Existential types (besides wildcard types) can be written and inferred
  "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
  "-Ycache-macro-class-loader:last-modified"   // and macro definitions. This can lead to performance improvements.
)

ThisBuild / resolvers ++= myResolvers

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    name := "tocen-live",
    reStart / mainClass := Some("com.pinkstack.tocenlive.Main"),
    assembly / mainClass := Some("com.pinkstack.tocenlive.Main"),
    assembly / assemblyJarName := "tocen-live.jar",
    assembly / assemblyMergeStrategy := {
      case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)

    },
    libraryDependencies ++= List(
      cats,
      circe,
      fs2,
      sttp,
      http4s,
      configurationLibs,
      logging,
      testingLibs
    ).foldLeft(Seq.empty[ModuleID])(_ ++ _)
  )
  .settings(
    Universal / mappings += (Compile / packageBin).value -> "tocen-live.jar",
    dockerUsername := Some("pinkstack"),
    dockerUpdateLatest := true,
    dockerBaseImage := "azul/zulu-openjdk:17-jre",
    dockerRepository := Some("ghcr.io"),
    dockerExposedPorts := Seq(8077),
    dockerExposedUdpPorts := Seq.empty[Int],
    dockerCommands := dockerCommands.value.flatMap {
      case add @ Cmd("RUN", args @ _*) if args.contains("id") =>
        List(
          Cmd("LABEL", "maintainer Oto Brglez <otobrglez@gmail.com>"),
          Cmd("LABEL", "org.opencontainers.image.url https://github.com/pinkstack/tocen-live"),
          Cmd("LABEL", "org.opencontainers.image.source https://github.com/pinkstack/tocen-live"),
          Cmd("ENV", "SBT_VERSION", sbtVersion.value),
          Cmd("ENV", "SCALA_VERSION", scalaVersion.value),
          Cmd("ENV", "TOCEN_LIVE_VERSION", version.value),
          add
        )
      case other => List(other)
    }
  )

lazy val deployWithAnsible = taskKey[Unit]("Execute the shell script")
deployWithAnsible := {
  """ansible-playbook playbooks/update-agents.yml -e tocen_api_key="%s" -vv"""
    .format(sys.env("TOCEN_API_KEY")) !
}
