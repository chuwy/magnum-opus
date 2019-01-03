package me.chuwy.magnumopus.server

import cats.Monad
import cats.implicits._
import cats.effect.Sync

import io.circe.syntax._

import org.http4s.rho._
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.circe._

import Model._

object Routes {
  def get[F[_]: Sync](dao: NotesDao[F]): RhoRoutes[F] =
    new RhoRoutes[F] {
      private val swaggerSupport = SwaggerSupport[F]
      import swaggerSupport._

      "List all notes" **
        GET / "notes" |>> {
        for {
          notes <- dao.listNotes
          result <- Ok(notes.asJson)
        } yield result
      }

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

