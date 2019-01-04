package me.chuwy.magnumopus

import cats.data.{ReaderT, StateT, State}
import cats.effect.IO
import doobie.util.transactor.Transactor
import me.chuwy.magnumopus.server.notes.Model.Note

package object server {
  /** Real-world effect, having an access to DB */
  type RIOF[F[_], A] = ReaderT[F, Transactor[F], A]
  type RIO[A]        = RIOF[IO, A]

  /** */
  type NotesST[F[_], A] = StateT[F, List[Note], A]

  /** Can be lifted into `NotesST` with `state.transformF(IO.eval)` */
  type NotesS[A] = State[List[Note], A]
}
