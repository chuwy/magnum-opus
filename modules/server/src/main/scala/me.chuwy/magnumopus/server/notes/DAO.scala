package me.chuwy.magnumopus.server.notes

import java.util.UUID

import cats.~>
import cats.data.{ReaderT, State}
import cats.implicits._

import doobie.implicits._

import me.chuwy.magnumopus.server.{NotesS, RIO}
import me.chuwy.magnumopus.server.notes.Model._

trait DAO[F[_]] { self =>
  def listNotes: F[List[Note]]
  def addNote(note: Note): F[Unit]
  def getNoteByStub(stub: String): F[Option[Note]]
  def getNoteById(id: UUID): F[Option[Note]]

  def mapK[G[_]](f: F ~> G): DAO[G] = new DAO[G] {
    def listNotes: G[List[Note]] = f(self.listNotes)
    def addNote(note: Note): G[Unit] =  f(self.addNote(note))
    def getNoteByStub(stub: String): G[Option[Note]] = f(self.getNoteByStub(stub))
    def getNoteById(id: UUID): G[Option[Note]] = f(self.getNoteById(id))
  }
}

object DAO {

  def apply[F[_]](implicit ev: DAO[F]): DAO[F] = ev

  // TODO: find what's the analog of RIO in doobie. `ConnectionIO`?
  implicit val realNotes: DAO[RIO] = new DAO[RIO] {
    def addNote(note: Note): RIO[Unit] = ???

    def listNotes: RIO[List[Note]] =
      ReaderT { xa => sql"SELECT * FROM notes".query[Note].to[List].transact(xa) }

    def getNoteByStub(stub: String): RIO[Option[Note]] =
      ???

    def getNoteById(id: UUID): RIO[Option[Note]] = ???
  }

  // TODO: it cannot server real-world app
  implicit val inMemoryNotes: DAO[NotesS] = new DAO[NotesS] {
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
