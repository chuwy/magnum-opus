package me.chuwy.magnumopus.server.notes

import cats.effect.Sync
import org.http4s.circe._
import org.http4s.rho.RhoRoutes
import org.http4s.rho.swagger.SwaggerSupport

import cats.implicits._
import io.circe.syntax._

import Model._


object Routes {
  /**
    *
    * @param dao Data Access Object
    * @tparam F effect performed during routing. Cannot get rid of `Sync` because of decoding
    * @return
    */
  def build[F[_]: Sync: DAO]: RhoRoutes[F] =
    new RhoRoutes[F] {
      private val swaggerSupport = SwaggerSupport[F]
      import swaggerSupport._

      "List all notes" **
        GET / "notes" |>> { () =>
        for {
          notes <- DAO[F].listNotes
          result <- Ok(notes.asJson)
        } yield result
      }

      "Find a note by stub" **
        GET / "notes" / pathVar[String]("stub", "Stub generated from note's title") |>> {
        stub: String => Ok("result")
      }

      "Create a new note" **
        POST / "notes" ^ jsonOf[F, Note] |>> {
        note: Note => {
          println(note)
          for {
            _ <- DAO[F].addNote(note)
          } yield Ok(s"Added $note")
        }
      }
    }
}
