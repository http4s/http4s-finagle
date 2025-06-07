val scala213 = "2.13.8"
val scala212 = "2.12.15"
val dotty = "3.0.2"

val Http4sVersion = "0.23.21"
val FinagleVersion = "24.2.0"
val supportedScalaVersions = List(scala213,scala212,dotty)

inScope(Scope.GlobalScope)(
  List(
    licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/http4s/http4s-finagle")),
    developers := List(
      Developer("jcouyang", "Jichao Ouyang", "oyanglulu@gmail.com", url("https://github.com/jcouyang"))
    ),
    scalaVersion := dotty,
    tlBaseVersion := Http4sVersion.split("\\.").take(2).mkString(".")
  )
)


lazy val root = (project in file("."))
  .settings(
    name := "Http4s Finagle",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.http4s"  %% "http4s-core" % Http4sVersion,
      "org.http4s"  %% "http4s-client" % Http4sVersion,
      "com.twitter" %% "finagle-http" % FinagleVersion,
      "org.typelevel" %% "case-insensitive" % "1.4.2",
      "org.http4s"  %% "http4s-dsl" % Http4sVersion % Test,
    ).map(_.cross(CrossVersion.for3Use2_13)) ++ Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
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
      case version if version == dotty =>  Seq(
      "-siteroot", "docs",
      "-d", "docs/_site",
      "-project-version", s"$Http4sVersion-$FinagleVersion",
      "-project-url", "https://github.com/http4s/http4s-finagle",
      "-Yerased-terms",
      )
      case _ => Seq()
    })
  )
