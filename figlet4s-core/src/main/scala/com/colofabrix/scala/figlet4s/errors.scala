package com.colofabrix.scala.figlet4s

import cats.MonadError
import cats.data._

object errors {

  //  Error management  //

  /**
   * MonadError specialized for Throwable errors
   *
   * @tparam F A higher-kinded type for which there is MonadError instance
   */
  type MonadThrowable[F[_]] = MonadError[F, Throwable]
  def MonadThrowable[F[_]: MonadThrowable]: MonadThrowable[F] = MonadError.apply[F, Throwable]

  /**
   * The outcome of an operation that might result in one or more errors
   *
   * @tparam A The type of the valid value contained in a FigletResult
   */
  type FigletResult[+A] = Validated[NonEmptyChain[FigletError], A]

  /**
   * Generic exception that can occur in the Figlet4s library
   */
  sealed trait FigletError extends Throwable {
    /** The description of the error */
    def message: String
  }

  object FigletError {
    def unapply(error: FigletError): Option[String] = Some(error.message)
  }

  /**
   * An exception in FIGlet
   *
   * @param inner The generic Throwable exception that caused the failure
   */
  final case class FigletException(inner: Throwable) extends FigletError {
    /** The description of the error */
    def message: String = inner.getMessage
  }

  /**
   * An error that can occur when loading from file
   *
   * @param message The description of the error
   * @param inner   The generic Throwable exception that caused the failure
   */
  final case class FigletLoadingError(message: String, inner: Throwable) extends FigletError

  /**
   * An error that can occur when interpreting a FIGLet file
   */
  sealed trait FLFError extends FigletError

  /**
   * An error that can occur when interpreting a FIGheader
   *
   * @param message The description of the error
   */
  final case class FIGheaderError(message: String) extends FLFError

  /**
   * An error that can occur when interpreting a FIGcharacter
   *
   * @param message The description of the error
   */
  final case class FIGcharacterError(message: String) extends FLFError

  /**
   * An error that can occur when interpreting a FIGfont
   *
   * @param message The description of the error
   */
  final case class FIGFontError(message: String) extends FLFError

}
