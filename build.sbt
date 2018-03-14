//import scalariform.formatter.preferences._

name := """qmeter-crm"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
//#routesGenerator := StaticRoutesGenerator
routesGenerator := InjectedRoutesGenerator

//resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value
//
//resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//
//resolvers += Resolver.sonatypeRepo("snapshots")


libraryDependencies += guice
libraryDependencies += specs2 % Test
libraryDependencies ++= Seq(
//  "com.mohiva"              %%  "play-silhouette"           % "3.0.2",
//  "org.webjars"             %%  "webjars-play"              % "2.4.0-1",
//  "net.codingwell"          %%  "scala-guice"               % "4.0.0",
//  "net.ceedubs"             %%  "ficus"                     % "1.1.2",

//  "com.typesafe.play"       %   "sbt-plugin"                % "2.4.x",

  "com.typesafe.play"       %%  "play-slick"                % "3.0.0",
  "com.typesafe.play"       %%  "play-slick-evolutions"     % "3.0.0",
  "org.postgresql"          %   "postgresql"                % "9.4-1201-jdbc41",
  "com.github.tototoshi"    %%  "slick-joda-mapper"         % "2.3.0",
  "joda-time"               %   "joda-time"                 % "2.7",
  "org.joda"                %   "joda-convert"              % "1.7",
  "com.typesafe.play"       %   "play-json-joda_2.12"       % "2.6.0",

  "org.scalatestplus.play"  %%  "scalatestplus-play"        % "3.1.2"     % Test
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"



//lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalacOptions ++= Seq(
//  "-deprecation", // Emit warning and location for usages of deprecated APIs.
//  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
//  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
//  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
//  "-Xlint", // Enable recommended additional warnings.
//  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
//  "-Ywarn-dead-code", // Warn when dead code is identified.
//  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
//  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
//  "-Ywarn-numeric-widen" // Warn when numerics are widened.
//)

//********************************************************
// Scalariform settings
//********************************************************

//defaultScalariformSettings
//
//ScalariformKeys.preferences := ScalariformKeys.preferences.value
//  .setPreference(FormatXml, false)
//  .setPreference(DoubleIndentClassDeclaration, false)
//  .setPreference(PreserveDanglingCloseParenthesis, true)