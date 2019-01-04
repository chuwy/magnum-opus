package me.chuwy.magnumopus.server

import cats.implicits._
import cats.effect._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    Config.CommandLine.parse(args) match {
      case Right(config) =>
        val httpApp = Bootstrap.getApp(config)
        val server = Service.getServer[IO](config, httpApp)
        server.compile.last.map(_.getOrElse(ExitCode.Error))
      case Left(error) =>
        IO(System.err.println(error)) *> IO.pure(ExitCode.Error)
    }
  }
}
