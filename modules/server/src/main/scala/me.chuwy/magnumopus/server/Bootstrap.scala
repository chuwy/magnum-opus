package me.chuwy.magnumopus
package server

import cats.{Monad, ~>}
import cats.arrow.FunctionK
import cats.data.{ReaderT, State, StateT}
import cats.effect.{ContextShift, IO}
import org.http4s.{HttpApp, Request, Response}
import org.http4s.syntax.kleisli._
import doobie.util.transactor.Transactor
import me.chuwy.magnumopus.server.Config.ServerConfig
import me.chuwy.magnumopus.server.notes.DAO
import me.chuwy.magnumopus.server.notes.Model.Note

object Bootstrap {
  /** Get `HttpApp` */
  def getApp(config: ServerConfig)(implicit cs: ContextShift[IO]): HttpApp[IO] =
    config.dbConfig match {
      case Config.Postgres(host, port, user, password) =>
        val xa = Transactor.fromDriverManager[IO](
          "org.postgresql.Driver", s"jdbc:postgresql:$host", user, password
        )
        val routes = notes.Routes.build[RIOF[IO, ?]].toRoutes().orNotFound
        injectTransactor[IO](xa)(routes)
      case Config.InMemory =>
        val routes: HttpApp[NotesST[IO, ?]] = notes.Routes.build[NotesST[IO, ?]].toRoutes().orNotFound
        injectState[IO](Nil)(routes)
    }

  private def liftState[S, A](state: State[S, A]): StateT[IO, S, A] =
    StateT[IO, S, A] { s => IO.eval(state.run(s)) }

  implicit def dao: DAO[NotesST[IO, ?]] = implicitly[DAO[NotesS]].mapK(new ~>[NotesS, NotesST[IO, ?]] {
    def apply[A](fa: NotesS[A]): NotesST[IO, A] = liftState(fa)
  })

  def injectTransactor[F[_]: Monad](xa: Transactor[F])
                                   (routes: HttpApp[RIOF[F, ?]]): HttpApp[F] = {
    def toF[A](fa: RIOF[F, A]): F[A] = fa.run(xa)
    def fromF[A](fa: F[A]): RIOF[F, A] = ReaderT.liftF(fa)
    inject(FunctionK.lift(toF), FunctionK.lift(fromF))(routes)
  }

  def injectState[F[_]: Monad](state: List[Note])
                              (routes: HttpApp[NotesST[F, ?]]): HttpApp[F] = {
    def toF[A](fa: NotesST[F, A]): F[A] = {
      println("TOFFF")
      val q = fa.runA(state)
      println(q)
      q
    }
    def fromF[A](fa: F[A]): NotesST[F, A] = StateT.liftF(fa)
    inject(FunctionK.lift(toF), FunctionK.lift(fromF))(routes)
  }

  def inject[G[_[_], _], F[_]: Monad](tof: G[F, ?] ~> F, fromf: F ~> G[F, ?])(routes: HttpApp[G[F, ?]]): HttpApp[F] =
    routes.mapK(tof).dimap[Request[F], Response[F]](_.mapK(fromf))(_.mapK(tof))
}
