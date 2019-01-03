package me.chuwy.magnumopus.server

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import com.monovore.decline.{Command, Opts}
import org.http4s.syntax.kleisli._
import Config.ServerConfig
import doobie.util.transactor.Transactor

object Main extends IOApp {

  val host = Opts.option[String]("host", "Host to bind a server").withDefault("127.0.0.1")
  val port = Opts.option[Int]("port", "Port to bind a server").withDefault(80)
  val server = Command("magnum-opus-server", "Magnum Opus HTTP Server") { (host, port).mapN(ServerConfig.apply) }

  def run(args: List[String]): IO[ExitCode] = {
    server.parse(args) match {
      case Right(config) =>
        val xa: Transactor[IO] = ???
        val dao = new Data.RealNotes(xa)
        val routes = Routes.get[IO](dao).toRoutes()
        Service.getServer[IO](config, routes.orNotFound).compile.last.map(_.getOrElse(ExitCode.Error))
      case Left(error) =>
        IO(System.err.println(error)) *> IO.pure(ExitCode.Error)
    }
  }
}
