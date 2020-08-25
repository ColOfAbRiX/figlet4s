package com.colofabrix.scala.figlet4s.unsafeops

import cats._
import cats.effect._

private[figlet4s] object SyncId {

  /**
   * Sync instance for Id for impure calculations
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit val syncId: Sync[Id] = new Sync[Id] {
    private val M: Monad[Id] = Monad[Id](catsInstancesForId)

    def pure[A](x: A): Id[A] =
      M.pure(x)

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      M.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      M.tailRecM(a)(f)

    def suspend[A](thunk: => Id[A]): Id[A] =
      thunk

    def raiseError[A](e: Throwable): Id[A] =
      throw e

    def bracketCase[A, B](resource: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] =
      throw new NotImplementedError("Sync[Id] doesn not support Bracket.bracketCase")

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      throw new NotImplementedError("Sync[Id] doesn not support ApplicativeError.handleErrorWith")
  }

}
