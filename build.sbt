name := """qmeter-crm"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
//#routesGenerator := StaticRoutesGenerator

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.typesafe.play"       %%  "play-slick"             % "3.0.0",
  "com.typesafe.play"       %%  "play-slick-evolutions"  % "3.0.0",
  "org.postgresql"          %   "postgresql"             % "9.4-1201-jdbc41",
  "com.github.tototoshi"    %%  "slick-joda-mapper"      % "2.3.0",
  "joda-time"               %   "joda-time"              % "2.7",
  "org.joda"                %   "joda-convert"           % "1.7",
  "com.typesafe.play"       %   "play-json-joda_2.12"    % "2.6.0",

  "org.scalatestplus.play"  %% "scalatestplus-play"     % "3.1.2"     % Test
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
