package me.chuwy.magnumopus.server

import java.util.UUID

import io.circe.Decoder


object Model {
  case class Note(content: String, id: UUID)

  implicit val decoder = Decoder.instance { cursor =>
    for {
      content <- cursor.downField("content").as[String]
      id <- cursor.downField("id").as[UUID]
    } yield Note(content, id)
  }
}
