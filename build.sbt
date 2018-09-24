import sbt.Keys.libraryDependencies

ThisBuild / organization := "com.zoltanmagyar"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val tamagotchi = (project in file("."))
  .settings(
    name := "Tamagotchi",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.16",
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.16" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  )