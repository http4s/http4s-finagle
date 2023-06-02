addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"       % "1.6.1")
addSbtPlugin("io.spray"                  % "sbt-revolver"        % "0.10.0")
addSbtPlugin("org.http4s"                % "sbt-http4s-org"      % "0.14.12")
libraryDependencies ++= Seq("us.oyanglul" %% "dhall-generic" % "0.3.79")
