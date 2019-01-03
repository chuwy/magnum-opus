package me.chuwy.magnumopus.server

import cats.Monad
import cats.effect.Sync

import org.http4s.rho._
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.circe._

import Model._

object Routes {
  def get[F[_]: Monad: Sync]: RhoRoutes[F] =
    new RhoRoutes[F] {
      private val swaggerSupport = SwaggerSupport[F]
      import swaggerSupport._

      "Find a note by stub" **
        GET / "notes" / pathVar[String]("stub", "Stub generated from note's title") |>> {
        stub: String => Ok("result")
      }

      "Create a new note" **
        POST / "notes" ^ jsonOf[F, Note] |>> {
        note: Note => Ok("result")
      }
    }
}

