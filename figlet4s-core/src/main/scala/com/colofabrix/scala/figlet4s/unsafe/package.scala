package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import com.colofabrix.scala.figlet4s.errors._

/**
 * Figlet4s interfaces that do not use effects
 */
package object unsafe extends OptionsBuilderMixin with FIGureMixin {

  /**
   * Sync instance for Id for impure calculations
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] val syncId: Sync[Id] = new Sync[Id] {
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
      throw new NotImplementedError("Sync[Id] does not support Bracket.bracketCase")

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      throw new NotImplementedError("Sync[Id] does not support ApplicativeError.handleErrorWith")
  }

  /**
   * Unsafely returns the value inside the FigletResult or throws an exception with the first error
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] class FigletResultOps[E, A](private val self: FigletResult[A]) extends AnyVal {
    @throws(classOf[FigletError])
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

}
