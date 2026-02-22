val scala213 = "2.13.18"
val scala212 = "2.12.20"

val Http4sVersion = "0.23.21"
val FinagleVersion = "24.2.0"
val supportedScalaVersions = List(scala213, scala212)

ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / homepage := Some(url("https://github.com/http4s/http4s-finagle"))
ThisBuild / developers := List(
  Developer("jcouyang", "Jichao Ouyang", "oyanglulu@gmail.com", url("https://github.com/jcouyang"))
)
ThisBuild / tlBaseVersion := Http4sVersion.split("\\.").take(2).mkString(".")
ThisBuild / startYear := Some(2020)
ThisBuild / crossScalaVersions := supportedScalaVersions
ThisBuild / scalaVersion := supportedScalaVersions.head

lazy val root = (project in file("."))
  .settings(
    name := "Http4s Finagle",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % Http4sVersion,
      "org.http4s" %% "http4s-client" % Http4sVersion,
      "com.twitter" %% "finagle-http" % FinagleVersion,
      "org.typelevel" %% "case-insensitive" % "1.4.2",
      "org.http4s" %% "http4s-dsl" % Http4sVersion % Test,
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
  )
