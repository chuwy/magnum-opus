package me.chuwy.magnumopus.server

import java.util.UUID

import cats.Monad
import cats.implicits._

import doobie.util.Read
import doobie.implicits._

import io.circe.{Decoder, Encoder, Json}

object Model {
  case class Note(content: String, id: UUID)

  implicit val decoder: Decoder[Note] =
    Decoder.instance { cursor =>
      for {
        content <- cursor.downField("content").as[String]
        id <- cursor.downField("id").as[UUID]
      } yield Note(content, id)
    }

  implicit val encoder: Encoder[Note] =
    Encoder.instance { note =>
      Json.fromFields(List(
        "id" -> Json.fromString(note.id.toString),
        "content" -> Json.fromString(note.content)
      ))
    }

  implicit val read: Read[Note] =
    (Read.fromGet[String], Read.fromGet[String].map(UUID.fromString)).mapN { (content, id) =>
      Note(content, id)
    }

  trait NotesDao[F[_]] {
    def listNotes(implicit F: Monad[F]): F[List[Note]]
    def addNote(note: Note): F[Unit]
    def getNoteByStub(stub: String): F[Option[Note]]
    def getNoteById(id: UUID): F[Option[Note]]
  }
}
