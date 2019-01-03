// SBT
import sbt._
import Keys._

// sbt-assembly
import sbtassembly._
import sbtassembly.AssemblyKeys._

object BuildSettings {

  lazy val common = Seq(
    version := "0.0.1-rc1",
    organization := "me.chuwy",
    scalaVersion := "2.12.8",
    initialCommands := "import me.chuwy.magnumopus._",

    scalacOptions := Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-Ywarn-dead-code",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused",
      "-Ywarn-value-discard",
      "-Ypartial-unification",
      "-language:higherKinds"
    ),
    javacOptions := Seq(
      "-source", "1.8",
      "-target", "1.8",
      "-Xlint"
    )
  )

  // Makes package (build) metadata available withing source code
  lazy val scalifySettings = Seq(
    sourceGenerators in Compile += Def.task {
      val file = (sourceManaged in Compile).value / "settings.scala"
    IO.write(file, """package me.chuwy.magnumopus.generated
                      |object ProjectMetadata {
                      |  val version = "%s"
                      |  val name = "%s"
                      |  val organization = "%s"
                      |  val scalaVersion = "%s"
                      |}
                      |""".stripMargin.format(version.value, name.value, organization.value, scalaVersion.value))
      Seq(file)
    }.taskValue
  )

  // sbt-assembly settings
  lazy val assemblySettings = Seq(
    assemblyJarName in assembly := { moduleName.value + "-" + version.value + ".jar" }
  )
}
