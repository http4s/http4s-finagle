import Dependencies._

val scala213 = "2.13.3"
val scala212 = "2.12.12"
val dotty = "0.26.0"
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
    scalaVersion := scala213
  )
)

val Http4sVersion = "1.0.0-M5"
val FinagleVersion = "20.9.0"

lazy val root = (project in file("."))
  .settings(
    name := "Http4s Finagle",
    crossScalaVersions := supportedScalaVersions,
    scalacOptions ++= Seq("-language:implicitConversions"),
    libraryDependencies ++= Seq(
      "org.http4s"  %% "http4s-core" % Http4sVersion,
      "org.http4s"  %% "http4s-client" % Http4sVersion,
      "com.twitter" %% "finagle-http" % FinagleVersion,
      "org.http4s"  %% "http4s-dsl" % Http4sVersion % Test,
    ).map(_.withDottyCompat(scalaVersion.value)) ++ Seq(
      "org.scalameta" %% "munit" % "0.7.9" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.9" % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / scalacOptions ++= (scalaVersion.value match {
      case version if version == scala213 =>  Seq(
        "-Ywarn-unused:imports",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:privates",
        "-Xfatal-warnings",
        "-deprecation",
      )
      case _ => Seq()
    }),
    Compile / doc / scalacOptions ++= (scalaVersion.value match {
      case version if List(dotty).contains(version) =>  Seq(
      "-siteroot", "docs",
      "-project", "Http4s Finagle",
      "-project-version", s"$Http4sVersion-$FinagleVersion",
      "-project-url", "https://github.com/http4s/http4s-finagle",
      "-Yerased-terms",
      )
      case _ => Seq()
    })
  )
