package me.chuwy.magnumopus.server

import java.util.UUID

import doobie._
import doobie.implicits._
import Model._
import cats.Monad

object Data {
  class RealNotes[F[_]](xa: Transactor[F]) extends NotesDao[F] {
    def addNote(note: Note): F[Unit] = ???

    def listNotes(implicit F: Monad[F]): F[List[Note]] =
      sql"SELECT * FROM notes".query[Note].to[List].transact(xa)

    def getNoteByStub(stub: String): F[Option[Note]] = ???

    def getNoteById(id: UUID): F[Option[Note]] = ???
  }
}
