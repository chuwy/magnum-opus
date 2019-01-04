package me.chuwy.magnumopus.server.notes

import java.util.UUID

import cats.implicits._

import doobie.util.Read

import io.circe.{Decoder, Encoder, Json}

object Model {
  case class Note(content: String, id: UUID, stub: String)

  implicit val noteDecoder: Decoder[Note] =
    Decoder.instance { cursor =>
      for {
        content <- cursor.downField("content").as[String]
        stub <- cursor.downField("stub").as[String]
        id <- cursor.downField("id").as[UUID]
      } yield Note(content, id, stub)
    }

  implicit val noteEncoder: Encoder[Note] =
    Encoder.instance { note =>
      Json.fromFields(List(
        "id" -> Json.fromString(note.id.toString),
        "stub" -> Json.fromString(note.stub),
        "content" -> Json.fromString(note.content)
      ))
    }

  implicit val readNote: Read[Note] =
    (Read.fromGet[String], Read.fromGet[String].map(UUID.fromString), Read.fromGet[String]).mapN {
      (content, id, stub) => Note(content, id, stub)
    }
}
