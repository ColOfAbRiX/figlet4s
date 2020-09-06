package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import com.colofabrix.scala.figlet4s.errors._
import scala.util._

package object either extends FIGureOps with OptionsBuilderOps {

  type FigletEither[+A] = Either[FigletError, A]

  /**
   * Sync instance for Either
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[either] val syncEither: Sync[FigletEither] = new Sync[FigletEither] {
    import cats.implicits._

    private val M: Monad[FigletEither] = Monad[FigletEither](catsStdInstancesForEither)

    def pure[A](x: A): FigletEither[A] =
      M.pure(x)

    def flatMap[A, B](fa: FigletEither[A])(f: A => FigletEither[B]): FigletEither[B] =
      M.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => FigletEither[Either[A, B]]): FigletEither[B] =
      M.tailRecM(a)(f)

    def suspend[A](thunk: => FigletEither[A]): FigletEither[A] =
      Try(thunk)
        .toEither
        .flatMap(identity)
        .leftFlatMap(raiseError)

    def raiseError[A](t: Throwable): FigletEither[A] =
      t match {
        case fe: FigletError => Left(fe)
        case t: Throwable    => Left(FigletException(t))
      }

    // format: off
    def bracketCase[A, B](acquire: FigletEither[A])(use: A => FigletEither[B])(release: (A, ExitCase[Throwable]) => FigletEither[Unit]): FigletEither[B] =
      throw new NotImplementedError("Sync[FigletEither] doesn not support Bracket.bracketCase")
    // format: on

    def handleErrorWith[A](fa: FigletEither[A])(f: Throwable => FigletEither[A]): FigletEither[A] =
      fa match {
        case Left(value) => f(value)
        case Right(_)    => fa
      }
  }

  /**
   * Transforms the FigletResult into a Either capturing the first error in the Left side
   */
  implicit private[either] class FigletResultOps[E, A](private val self: FigletResult[A]) extends AnyVal {
    def asEither: FigletEither[A] = self.fold(e => Left(e.head), Right(_))
  }

  /**
   * Unsafely returns the value inside the FigletEither or throws an exception with the first error
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[either] class FigletEitherOps[A](private val self: FigletEither[A]) extends AnyVal {
    @throws(classOf[FigletError])
    def unsafeGet: A = self.fold(e => throw e, identity)
  }

}
