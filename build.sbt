import Dependencies._

val scala213 = "2.13.1"
val scala212 = "2.12.10"
val dotty = "0.24.0-RC1"

val supportedScalaVersions = List(scala213,scala212,dotty)

inScope(Scope.GlobalScope)(
  List(
    organization := "org.http4s",
    licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/http4s/http4s-finagle")),
    developers := List(
      Developer("jcouyang", "Jichao Ouyang", "oyanglulu@gmail.com", url("https://github.com/jcouyang"))
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/http4s/http4s-finagle"),
        "scm:git@github.com:http4s/http4s-finagle.git"
      )
    ),
    pgpPublicRing := file(".") / ".gnupg" / "pubring.asc",
    pgpSecretRing := file(".") / ".gnupg" / "secring.asc",
    releaseEarlyWith := SonatypePublisher,
    /* TODO: Everything compile in dotty, BUT runtime error
     java.lang.NoSuchMethodError: org.http4s.dsl.io$.GET()Lorg/http4s/Method$PermitsBody;
     */
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalaVersion := dotty
  )
)

val http4sVersion = settingKey[String]("Http4sVersion")
val FinagleVersion = "20.4.1"
http4sVersion := "0.21.4"
lazy val root = (project in file("."))
  .settings(
    name := "Http4s Finagle",
    crossScalaVersions := supportedScalaVersions,
    scalacOptions ++= Seq("-language:implicitConversions"),
    libraryDependencies ++= Seq(
      "org.http4s"  %% "http4s-core" % http4sVersion.value,
      "org.http4s"  %% "http4s-client" % http4sVersion.value,
      "org.http4s"  %% "http4s-dsl" % http4sVersion.value % Test,
      "com.twitter" %% "finagle-http" % FinagleVersion,
    ).map(_.withDottyCompat(scalaVersion.value)) ++ Seq(
      "org.scalameta" %% "munit" % "0.7.6" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.6" % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
  )
