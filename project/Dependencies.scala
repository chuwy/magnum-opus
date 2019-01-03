import sbt._

object Dependencies {

  object V {
    // Scala
    val decline       = "0.6.0"
    val doobie        = "0.6.0"
    val http4s        = "0.20.0-M4"
    val rho           = "0.19.0-M4"
    val laika         = "0.10.0"
    // Scala (test only)
    val specs2        = "4.3.5"
    val scalaCheck    = "1.14.0"
  }

  // Scala
  val decline       = "com.monovore"               %% "decline"                   % V.decline
  val doobie        = "org.tpolecat"               %% "doobie-core"               % V.doobie
  val http4s        = "org.http4s"                 %% "http4s-core"               % V.http4s
  val http4sServer  = "org.http4s"                 %% "http4s-blaze-server"       % V.http4s
  val http4sClient  = "org.http4s"                 %% "http4s-blaze-client"       % V.http4s
  val http4sCirce   = "org.http4s"                 %% "http4s-circe"              % V.http4s

  val rho           = "org.http4s"                 %% "rho-swagger"               % V.rho
  val laika         = "org.planet42"               %% "laika-core"                % V.laika

  // Scala (test only)
  val specs2        = "org.specs2"                 %% "specs2-core"               % V.specs2         % "test"
  val scalaCheck    = "org.scalacheck"             %% "scalacheck"                % V.scalaCheck     % "test"
}
