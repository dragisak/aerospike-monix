organization := "com.dragishak"

name := "aerospike-monix"

version := "0.1"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.6")

val monixVersion = "3.0.0-RC1"

val aerospikeVersion = "4.1.9"


libraryDependencies ++= List(
  "io.monix" %% "monix" % monixVersion,
  "com.aerospike" % "aerospike-client" % aerospikeVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "com.dimafeng" %% "testcontainers-scala" % "0.20.0" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.25" % Test
)

