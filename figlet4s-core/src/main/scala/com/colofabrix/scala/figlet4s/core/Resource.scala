package com.colofabrix.scala.figlet4s.core

import com.colofabrix.scala.figlet4s.errors._

/**
  * I want to support Id as an effect for this library so that non-FP users can avoid dealing with monads. It's not
  * possible to create a real Sync[Id] instance for Id because Id doesn't support a true suspension of effects.
  * Here I implement a version of opening a resource with a built-in suspension for the acquisition with exception
  * reporting in MonadError. See also: https://medium.com/@dkomanov/scala-try-with-resources-735baad0fd7d
  */
object Resource {

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  def withResource[F[_]: MonadThrowable, R <: AutoCloseable, A](resource: => R)(f: R => F[A]): F[A] = {
    var exception: Throwable = null
    try {
      f(resource)
    } catch {
      case e: Throwable =>
        exception = e
        MonadThrowable[F].raiseError[A](FigletLoadingError(e.getMessage, e))
    } finally {
      if (exception == null) resource.close()
      else
        try {
          resource.close()
        } catch {
          case suppressed: Throwable =>
            exception.addSuppressed(suppressed)
        }
    }
  }

}
