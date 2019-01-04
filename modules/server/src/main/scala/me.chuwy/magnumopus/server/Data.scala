package me.chuwy.magnumopus.server

import java.util.UUID

import cats.data.{ReaderT, State}
import cats.implicits._

import doobie._
import doobie.implicits._

import Model._

object Data {

  trait NotesDao[F[_]] {
    def listNotes: F[List[Note]]
    def addNote(note: Note): F[Unit]
    def getNoteByStub(stub: String): F[Option[Note]]
    def getNoteById(id: UUID): F[Option[Note]]
  }

  object NotesDao {
    def apply[F[_]](implicit ev: NotesDao[F]): NotesDao[F] = ev
  }

  implicit def realNotes = new NotesDao[RIO] {
    def addNote(note: Note): RIO[Unit] = ???

    def listNotes: RIO[List[Note]] =
      ReaderT { xa => sql"SELECT * FROM notes".query[Note].to[List].transact(xa) }

    def getNoteByStub(stub: String): RIO[Option[Note]] =
      ???

    def getNoteById(id: UUID): RIO[Option[Note]] = ???
  }

  implicit def inMemoryNotes = new NotesDao[NotesS] {
    def addNote(note: Note): NotesS[Unit] =
      State(notes => (note :: notes, ()))

    def listNotes: NotesS[List[Note]] =
      State.get

    def getNoteByStub(stub: String): NotesS[Option[Note]] =
      State(notes => (notes, notes.find(_.stub === stub)))

    def getNoteById(id: UUID): NotesS[Option[Note]] =
      State(notes => (notes, notes.find(_.id === id)))
  }
}
