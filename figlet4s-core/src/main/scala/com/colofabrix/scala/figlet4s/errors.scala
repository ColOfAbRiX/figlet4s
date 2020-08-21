package com.colofabrix.scala.figlet4s

import cats.data._
import cats.MonadError

object errors {

  //  Error management  //

  type MonadThrowable[F[_]] = MonadError[F, Throwable]
  def MonadThrowable[F[_]: MonadThrowable]: MonadThrowable[F] = MonadError.apply[F, Throwable]

  /**
   * A result of a processing operation that might include errors
   */
  type FigletResult[A] = Validated[NonEmptyChain[FigletError], A]

  /**
   * FIGLet Error
   */
  sealed trait FigletError extends Throwable

  /** A generic exception in the system */
  final case class FigletGenericException(message: String, inner: Throwable) extends FigletError

  /** An error when loading from files */
  final case class FigletLoadingError(message: String, inner: Throwable) extends FigletError

  /**
   * Errors for FIGLet Files
   */
  sealed trait FLFError extends FigletError

  /** FLF Header Error */
  final case class FIGheaderError(message: String) extends FLFError

  /** FLF Character Error */
  final case class FIGcharacterError(message: String) extends FLFError

  /** FLF Font Error */
  final case class FIGFontError(message: String) extends FLFError

}
