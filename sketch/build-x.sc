/*
import sbt._
import Keys._
import Dependencies._

import scala.sys.process._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / publishTo := Some(Resolver.file("Unused transient repository", file("target/voice-root")))
ThisBuild / publishArtifact := false

inThisBuild(Seq(
  organization := "com.pollpass.voice",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
  ),
))

val sharedSettings: Seq[SettingsDefinition] = Seq(
  publishArtifact := false,
  publish / skip := true,
  Compile / packageDoc / mappings := Seq.empty
)

lazy val backend = (project in file("backend"))
  .enablePlugins(BuildInfoPlugin, JavaAppPackaging, DockerPlugin)
  .settings(sharedSettings: _*)
  .settings(DockerSettings.dockerSettings: _*)
  .settings(
    name := "voice-backend",

    buildInfoPackage := "com.pollpass.voice",
    buildInfoKeys := Seq[BuildInfoKey](version, scalaVersion, sbtVersion,
      BuildInfoKey.action("javaVersion")(sys.props("java.version"))
    ),

    libraryDependencies ++= List(
      cats, circe, akka, configurationLibs, logging,
      googleLibs, scalacache, testingLibs, slick
    ).foldLeft(Seq.empty[ModuleID])(_ ++ _),

    // Compilation and running settings
    Compile / mainClass := Some("com.pollpass.voice.Boot"),
    Compile / run / fork := true
    // fork / run := true,
    // connectInput / run := true
    // run / fork := true,
    // run / connectInput := true

    ,
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/voice-backend")))
  )

lazy val frontend = (project in file("frontend"))
  .settings(sharedSettings: _*)
  .settings(
    name := "voice-frontend"
  )

*/
