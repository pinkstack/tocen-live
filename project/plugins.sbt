resolvers ++= Seq(
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
  "Artima Maven Repository" at "https://repo.artima.com/releases",
  Resolver.bintrayRepo("kamon-io", "sbt-plugins"),
  // Resolver.bintrayRepo("beyondthelines", "maven"),
  // "Seasar2 Repository" at "https://maven.seasar.org/maven2"
)

addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.1.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.1")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.4")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.3")
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.12")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
