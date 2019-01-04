package me.chuwy.magnumopus.server

import cats.implicits._

import com.monovore.decline.{Command, Opts}


object Config {
  sealed trait DbConfig extends Product with Serializable
  case class Postgres(host: String, port: Int, user: String, password: String) extends DbConfig
  case object InMemory extends DbConfig


  case class ServerConfig(host: String, port: Int, dbConfig: DbConfig)

  object CommandLine {
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

    def parse(args: List[String]) =
      server.parse(args)
  }
}
