package com.colofabrix.scala.figlet4s.core

import cats.effect._
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
@SuppressWarnings(Array("org.wartremover.warts.All"))
object Braket {

  def withResource[F[_]: MonadThrow, R <: AutoCloseable, A](resource: => R)(f: R => F[A]): F[A] = {
    var e: Option[Throwable] = None
    try {
      f(resource)
    }
    catch {
      case t: Throwable =>
        val f = FigletLoadingError(t.getMessage, t)
        e = Some(f)
        MonadThrow[F].raiseError[A](f)
    }
    finally {
      e.fold(resource.close()) { te =>
        try {
          resource.close()
        }
        catch {
          case t: Throwable =>
            te.addSuppressed(t)
        }
      }
    }
  }

}
