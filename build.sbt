import play.sbt.PlayScala

name := """qmeter-crm"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
//#routesGenerator := StaticRoutesGenerator
//routesGenerator := InjectedRoutesGenerator
//routesImport += "utils.route.Binders._"

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += guice
libraryDependencies += ehcache
//libraryDependencies += specs2 % Test
libraryDependencies ++= Seq(
  "com.mohiva"              %%  "play-silhouette"           % "5.0.3",
  "net.codingwell"          %%  "scala-guice"               % "4.1.1",
  "com.iheart"              %%  "ficus"                     % "1.4.3",

  "com.mohiva"              %%  "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva"              %%  "play-silhouette-persistence" % "5.0.0",
  "com.mohiva"              %%  "play-silhouette-crypto-jca" % "5.0.0",

  "com.typesafe.play"       %%  "play-slick"                % "3.0.0",
  "com.typesafe.play"       %%  "play-slick-evolutions"     % "3.0.0",
  "org.postgresql"          %   "postgresql"                % "9.4-1201-jdbc41",
  "com.github.tototoshi"    %%  "slick-joda-mapper"         % "2.3.0",
  "joda-time"               %   "joda-time"                 % "2.7",
  "org.joda"                %   "joda-convert"              % "1.7",
  "com.typesafe.play"       %   "play-json-joda_2.12"       % "2.6.0",

  "org.scalatestplus.play"  %%  "scalatestplus-play"        % "3.1.2"     % Test,
  "com.typesafe.play"       %%  "play-mailer"               % "6.0.1",
  "com.typesafe.play"       %%  "play-mailer-guice"         % "6.0.1",
  "com.enragedginger"       %%  "akka-quartz-scheduler"     % "1.6.1-akka-2.5.x",
  "com.adrianhurt"          %%  "play-bootstrap"            % "1.2-P26-B3"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"