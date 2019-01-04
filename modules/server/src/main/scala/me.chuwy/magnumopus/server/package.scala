package me.chuwy.magnumopus

import cats.data.{ReaderT, StateT, State}
import cats.effect.IO
import doobie.util.transactor.Transactor
import me.chuwy.magnumopus.server.Model.Note

package object server {
  /** */
  type RIO[A] = ReaderT[IO, Transactor[IO], A]

  /** */
  type NotesST[A] = StateT[IO, List[Note], A]

  /** Can be lifted into `NotesST` with `state.transformF(IO.eval)` */
  type NotesS[A] = State[List[Note], A]
}
