package me.chuwy.magnumopus.server

import cats.implicits._
import cats.effect.Sync
import io.circe.syntax._
import org.http4s.rho._
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.circe._
import Model._
import Data.NotesDao

object NotesRoutes {
  /**
    *
    * @param dao Data Access Object
    * @tparam F effect performed during routing. Cannot get rid of `Sync` because of decoding
    * @return
    */
  def build[F[_]: Sync: NotesDao]: RhoRoutes[F] =
    new RhoRoutes[F] {
      private val swaggerSupport = SwaggerSupport[F]
      import swaggerSupport._

      "List all notes" **
        GET / "notes" |>> { () =>
        for {
          notes <- NotesDao[F].listNotes
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

