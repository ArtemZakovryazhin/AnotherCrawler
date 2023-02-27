name := "AnotherCrawler"

version := "0.1"

scalaVersion := "2.13.10"

val akkaVersion = "2.7.0"
val akkaHttpVersion = "10.4.0"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
)