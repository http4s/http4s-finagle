addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"       % "1.6.1")
addSbtPlugin("io.spray"                  % "sbt-revolver"        % "0.9.1")
addSbtPlugin("ch.epfl.scala"             % "sbt-release-early"   % "2.1.1")
libraryDependencies ++= Seq("us.oyanglul" %% "dhall-generic" % "0.3.79")
