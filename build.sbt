name := "json-schema-renderer"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)