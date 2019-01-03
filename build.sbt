lazy val core = project.in(file("modules/core"))
  .settings(
    name := "magnumopus",
  )
  .settings(BuildSettings.common)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.decline,
      Dependencies.doobie,
      Dependencies.http4s,
      Dependencies.laika,

      Dependencies.specs2,
      Dependencies.scalaCheck
    )
  )

lazy val server = project.in(file("modules/server"))
  .settings(
    name := "magnumopus-server",
    description := "HTTP Server",
  )
  .settings(BuildSettings.common)
  .settings(
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      Dependencies.decline,
      Dependencies.doobie,
      Dependencies.http4s,
      Dependencies.http4sServer,
      Dependencies.http4sCirce,
      Dependencies.rho,
      Dependencies.laika,

      Dependencies.specs2,
      Dependencies.scalaCheck
    )
  )

lazy val cliClient = project.in(file("modules/cli-client"))
  .settings(
    name := "magnumopus-cli-client",
  )
  .settings(BuildSettings.common)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.decline,
      Dependencies.http4s,
      Dependencies.http4sClient,
      Dependencies.laika,

      Dependencies.specs2,
      Dependencies.scalaCheck
    )
  )
