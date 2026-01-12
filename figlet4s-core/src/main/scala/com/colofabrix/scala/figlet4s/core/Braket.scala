package com.colofabrix.scala.figlet4s.core

import cats.MonadThrow
import com.colofabrix.scala.figlet4s.errors._

/**
 * I want to support Id as an effect for this library so that non-FP users can avoid dealing with monads.
 *
 * It's not possible to create a real Sync[Id] instance for Id because Id doesn't support a true suspension of effects.
 * Here I implement a version of opening a resource with a built-in suspension for the acquisition with exception
 * reporting in MonadError and use this one to work with resources while Sync[Id] will just throw the exception.
 *
 * See: https://medium.com/@dkomanov/scala-try-with-resources-735baad0fd7d
 * See: https://stackoverflow.com/a/56089521/1215156
 */
object Braket {

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def withResource[F[_]: MonadThrow, R <: AutoCloseable, A](resource: => R)(f: R => F[A]): F[A] = {
    var exception: Option[Throwable] = None

    val result =
      try {
        val r = resource
        f(r)
      } catch {
        case t: Throwable =>
          val fle = FigletLoadingError(t.getMessage, t)
          exception = Some(fle)
          MonadThrow[F].raiseError[A](fle) // Discarded
      } finally {
        try {
          resource.close()
        } catch {
          case t: Throwable =>
            exception match {
              case Some(te) => te.addSuppressed(t)
              case None     => exception = Some(t)
            }
        }
      }

    exception.fold(result)(MonadThrow[F].raiseError[A](_))
  }

}
