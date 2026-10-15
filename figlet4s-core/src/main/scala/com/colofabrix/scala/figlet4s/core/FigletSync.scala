package com.colofabrix.scala.figlet4s.core

/**
 * Minimal effect abstraction used by Figlet4s to run impure operations
 *
 * It captures the small set of capabilities that the library needs: suspension of side effects, error reporting and
 * safe handling of AutoCloseable resources. Instances are provided for Id (eager evaluation, exceptions propagate) and
 * for the effect types offered by the extension modules.
 *
 * @tparam F A higher-kinded type for which there is a FigletSync instance
 */
trait FigletSync[F[_]] {

  /**
   * Wraps a pure value into the effect
   */
  def pure[A](a: A): F[A]

  /**
   * Sequences two effects, using the output of the first one as input of the second one
   */
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  /**
   * Transforms the value inside the effect
   */
  def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))

  /**
   * Suspends a side effect into the effect
   */
  def delay[A](thunk: => A): F[A]

  /**
   * Creates an effect that reports the given error
   */
  def raiseError[A](e: Throwable): F[A]

  /**
   * Handles the errors of an effect
   */
  def handleErrorWith[A](fa: F[A])(f: Throwable => F[A]): F[A]

  /**
   * Acquires a resource, uses it and ensures that the resource is closed after usage
   *
   * Errors thrown by the user function are wrapped into a
   * [[com.colofabrix.scala.figlet4s.errors.FigletLoadingError FigletLoadingError]]; errors thrown by the closing of
   * the resource are reported raw or suppressed on the main error.
   */
  def withResource[R <: AutoCloseable, A](acquire: => R)(use: R => F[A]): F[A]

}

object FigletSync {

  /**
   * Identity effect: values are evaluated eagerly and errors are thrown
   */
  type Id[A] = A

  /**
   * Returns the FigletSync instance for the given effect
   */
  def apply[F[_]](implicit F: FigletSync[F]): FigletSync[F] =
    F

}
