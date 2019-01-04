package me.chuwy.magnumopus.server

import cats.arrow.FunctionK
import cats.data.Kleisli
import cats.implicits._
import cats.effect._
import cats.~>
import org.http4s.{HttpApp, HttpRoutes, Request, Response}
//import cats.effect.implicits._
import com.monovore.decline.{Command, Opts}
import org.http4s.syntax.kleisli._
import Config.{Postgres, ServerConfig}
import Data._
import doobie.util.transactor.Transactor

object Main extends IOApp {

  val host = Opts.option[String]("host", "Host to bind a server").withDefault("127.0.0.1")
  val port = Opts.option[Int]("port", "Port to bind a server").withDefault(80)

  val dbHost = Opts.option[String]("dbhost", "PostgreSQL server host")
  val dbPort = Opts.option[Int]("dbport", "PostgreSQL server port")
  val dbUser = Opts.option[String]("dbuser", "PostgreSQL user")
  val dbPassword = Opts.option[String]("dbpassword", "PostgreSQL password")
  val dbConfig = Opts.flag("in-memory", "Store data in memory").map(_ => Config.InMemory).orElse {
    (dbHost, dbPort, dbUser, dbPassword).mapN(Postgres.apply)
  }

  val server = Command("magnum-opus-server", "Magnum Opus HTTP Server") {
    (host, port, dbConfig).mapN(ServerConfig.apply)
  }

  def injectTransactor(xa: Transactor[IO])(routes: HttpApp[RIO]): Kleisli[IO, Request[IO], Response[IO]] = {
    val toio: RIO ~> IO = new FunctionK[RIO, IO] {
      def apply[A](fa: RIO[A]): IO[A] = fa.run(xa)
    }
    val fromio: IO ~> RIO = new FunctionK[IO, RIO] {
      def apply[A](fa: IO[A]): RIO[A] = cats.data.ReaderT(_ => fa)
    }

    routes
      .mapK(toio)
      .dimap((request: Request[IO]) => request.mapK(fromio))(response => response.mapK(toio))
  }


  def run(args: List[String]): IO[ExitCode] = {
    server.parse(args) match {
      case Right(config) =>
        val xa = Transactor.fromDriverManager[IO](
          "org.postgresql.Driver", s"jdbc:postgresql:${config.dbConfig}", "postgres", ""
        )
        val routesR: HttpApp[RIO] = NotesRoutes.build[RIO].toRoutes().orNotFound
        val routes = injectTransactor(xa)(routesR)


        val server = Service.getServer[IO](config, routes)
        server.compile.last.map(_.getOrElse(ExitCode.Error))
      case Left(error) =>
        IO(System.err.println(error)) *> IO.pure(ExitCode.Error)
    }
  }
}
