package me.chuwy.magnumopus.server

object Config {
  sealed trait DbConfig extends Product with Serializable
  case class Postgres(host: String, port: Int, user: String, password: String) extends DbConfig
  case object InMemory extends DbConfig


  case class ServerConfig(host: String, port: Int, dbConfig: DbConfig)
}
