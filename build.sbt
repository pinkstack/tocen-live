import sbt._
import Keys._
import Dependencies._

import scala.sys.process._

ThisBuild / version := "0.0.1"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / publishArtifact := false
ThisBuild / organization := "com.pinkstack.tocenlive"
ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "utf-8", // Specify character encoding used by source files.
  "-unchecked",
  "-explaintypes", // Explain type errors in more detail.
  "-language:implicitConversions",
  "-language:existentials",
  "-language:dynamics",
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
  "-Ycache-macro-class-loader:last-modified", // and macro definitions. This can lead to performance improvements.

)

lazy val root = (project in file("."))
  .settings(
    name := "tocen-live",

    libraryDependencies ++= List(
      cats, circe, fs2, sttp, configurationLibs, logging,
      scalacache, testingLibs
    ).foldLeft(Seq.empty[ModuleID])(_ ++ _),

    resolvers ++= myResolvers
  )
