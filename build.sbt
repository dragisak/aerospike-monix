inThisBuild(
  List(
    organization := "com.dragishak",
    homepage := Some(url("https://github.com/dragisak/aerospike-monix")),
    licenses := List("MIT" -> url("https://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "dragisak",
        "Dragisa Krsmanovic",
        "dragishak@gmail.com",
        url("https://github.com/dragisak")
      )
    )
  )
)

name := "aerospike-monix"

scalaVersion := "2.12.6"

crossScalaVersions := List("2.12.6", "2.11.12")

val monixVersion = "3.0.0-RC1"

val aerospikeVersion = "4.1.9"

libraryDependencies ++= List(
  "io.monix" %% "monix" % monixVersion,
  "com.aerospike" % "aerospike-client" % aerospikeVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "com.dimafeng" %% "testcontainers-scala" % "0.20.0" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.25" % Test
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
