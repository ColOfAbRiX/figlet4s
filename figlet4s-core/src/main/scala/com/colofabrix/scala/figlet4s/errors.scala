package com.colofabrix.scala.figlet4s

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.ImplicitConversion"))
object errors {

  //  Error management  //

  /**
   * Generic exception that can occur in the Figlet4s library
   */
  sealed abstract class FigletException(message: String) extends RuntimeException(message)
  object FigletException {

    def unapply(error: FigletException): Option[String] = Some(error.getMessage)

  }

  /**
   * An error that occurred during the execution of Figlet4s
   *
   * @param message The description of the error
   */
  final class FigletError(message: String) extends FigletException(message) {

    def this(message: String, cause: Throwable) = {
      this(message)
      initCause(cause)
    }

  }

  object FigletError {

    def unapply(error: FigletError): Option[String] = Some(error.getMessage)
    def apply(cause: Throwable): FigletError        = new FigletError(cause.getMessage)
    def apply(message: String): FigletError         = new FigletError(message: String)
    def apply(message: String, cause: Throwable): FigletError =
      new FigletError(message: String, cause: Throwable)

  }

  /**
   * An error that can occur when loading from file
   *
   * @param message The description of the error
   */
  final class FigletLoadingError(message: String) extends FigletException(message) {

    def this(message: String, cause: Throwable) = {
      this(message)
      initCause(cause)
    }

  }

  object FigletLoadingError {

    def apply(message: String): FigletLoadingError = new FigletLoadingError(message: String)
    def apply(message: String, cause: Throwable): FigletLoadingError =
      new FigletLoadingError(message: String, cause: Throwable)

  }

  /**
   * An error that can occur when interpreting a FIGLet file
   */
  sealed abstract class FLFError(message: String) extends FigletException(message)

  /**
   * An error that can occur when interpreting a FIGheader
   *
   * @param message The description of the error
   */
  final class FIGheaderError(message: String) extends FigletException(message) {

    def this(message: String, cause: Throwable) = {
      this(message)
      initCause(cause)
    }

  }

  object FIGheaderError {

    def apply(message: String): FIGheaderError = new FIGheaderError(message: String)
    def apply(message: String, cause: Throwable): FIGheaderError =
      new FIGheaderError(message: String, cause: Throwable)

  }

  /**
   * An error that can occur when interpreting a FIGcharacter
   *
   * @param message The description of the error
   */
  final class FIGcharacterError(message: String) extends FLFError(message)

  object FIGcharacterError {

    def apply(message: String): FIGcharacterError = new FIGcharacterError(message: String)

  }

  /**
   * An error that can occur when interpreting a FIGfont
   *
   * @param message The description of the error
   */
  final class FIGFontError(message: String) extends FLFError(message) {

    def this(message: String, cause: Throwable) = {
      this(message)
      initCause(cause)
    }

  }

  object FIGFontError {

    def apply(message: String): FIGFontError = new FIGFontError(message: String)
    def apply(message: String, cause: Throwable): FIGFontError =
      new FIGFontError(message: String, cause: Throwable)

  }

}
