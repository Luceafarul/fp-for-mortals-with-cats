ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

scalacOptions in ThisBuild ++= Seq(
  "-Ymacro-annotations"
)

lazy val root = (project in file("."))
  .settings(
    name := "fp-for-mortals-with-cats",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.0",
      "org.typelevel" %% "cats-core" % "2.3.0",
      "org.typelevel" %% "simulacrum" % "1.0.0" % Test
    )
  )

addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full)
