// SBT
import sbt._
import Keys._

import sbtassembly._
import sbtassembly.AssemblyKeys._

// SBT Native Packager
import com.typesafe.sbt.packager.Keys.{daemonUser, maintainer}
import com.typesafe.sbt.packager.docker.{ ExecCmd, Cmd }
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

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

  lazy val dockerSettings = Seq(
    // Use single entrypoint script for all apps
    Universal / sourceDirectory := new File(baseDirectory.value, "scripts"),
    dockerRepository := Some("repo.treescale.com"),
    dockerUsername := Some("snowplow"),
    dockerBaseImage := "snowplow-docker-registry.bintray.io/snowplow/base-debian:0.1.0",
    Docker / maintainer := "Anton Parkhomenko <mailbox@chuwy.me>",
    Docker / daemonUser := "root",  // Will be gosu'ed by docker-entrypoint.sh
    dockerEntrypoint := Seq("docker-entrypoint.sh"),
    dockerCommands ++= Seq(
      ExecCmd("RUN", "cp", "/opt/docker/docker-entrypoint.sh", "/usr/local/bin/"),
      Cmd("RUN", "apt update"),
      Cmd("RUN", "mkdir -p /usr/share/man/man7"),
      Cmd("RUN", "apt install -y postgresql-client-9.6")
    ),
    dockerCmd := Seq("--help")
  )

  // sbt-assembly settings
  lazy val assemblySettings = Seq(
    assemblyJarName in assembly := { moduleName.value + "-" + version.value + ".jar" }
  )
}
