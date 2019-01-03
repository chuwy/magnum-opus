package me.chuwy.magnumopus.server

import cats.effect.{ConcurrentEffect, Timer}

import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder

import me.chuwy.magnumopus.server.Config.ServerConfig

object Service {
  def getServer[F[_]: ConcurrentEffect: Timer](config: ServerConfig, app: HttpApp[F]) =
    BlazeServerBuilder[F].withHttpApp(app).bindHttp(config.port, config.host).serve
}
