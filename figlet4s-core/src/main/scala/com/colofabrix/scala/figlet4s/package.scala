package com.colofabrix.scala

/**
 * Package that contains the definition of the outcome of Figlet4s operations and the syntax to work with it
 */
package object figlet4s {

  //  FigletResult syntax  //

  /**
   * Syntax on plain values
   */
  implicit class FigletValueOps[A](private val a: A) extends AnyVal {

    def valid: FigletResult[A] =
      FigletResult.Valid(a)

  }

  /**
   * Syntax on errors
   */
  implicit class FigletExceptionOps(private val e: errors.FigletException) extends AnyVal {

    def invalid: FigletResult[Nothing] =
      FigletResult.Invalid(e :: Nil)

  }

  /**
   * Syntax to convert an error message into an invalid outcome
   */
  implicit class FigletMessageOps(private val message: String) extends AnyVal {

    def toInvalid: FigletResult[Nothing] =
      FigletResult.Invalid(errors.FigletError(message) :: Nil)

  }

  /**
   * Syntax on Option, to lift an Option into a FigletResult
   */
  implicit class FigletOptionOps[A](private val option: Option[A]) extends AnyVal {

    def toValid(error: => errors.FigletException): FigletResult[A] =
      option.map(FigletResult.Valid(_)).getOrElse(FigletResult.Invalid(error :: Nil): FigletResult[A])

    def traverse[B](f: A => FigletResult[B]): FigletResult[Option[B]] =
      option.map(f(_).map(Some(_))).getOrElse(FigletResult.Valid(None))

  }

  /**
   * Syntax on Try, to lift a Try into a FigletResult
   */
  implicit class FigletTryOps[A](private val value: scala.util.Try[A]) extends AnyVal {

    def toValid(mapError: Throwable => errors.FigletException): FigletResult[A] =
      FigletResult.fromTry(value)(mapError)

  }

  /**
   * Syntax on sequences of values
   */
  implicit class FigletSeqOps[A](private val values: Seq[A]) extends AnyVal {

    def traverse[B](f: A => FigletResult[B]): FigletResult[Seq[B]] =
      FigletResult.sequence(values.map(f))

  }

  /**
   * Syntax on 2-tuples of results, accumulating the errors of both sides
   */
  implicit class FigletResultTuple2Ops[A, B](private val tuple: (FigletResult[A], FigletResult[B])) extends AnyVal {

    def mapN[C](f: (A, B) => C): FigletResult[C] =
      (tuple._1, tuple._2) match {
        case (FigletResult.Invalid(es1), FigletResult.Invalid(es2)) => FigletResult.Invalid(es1 ::: es2)
        case (FigletResult.Invalid(es), _)                          => FigletResult.Invalid(es)
        case (_, FigletResult.Invalid(es))                          => FigletResult.Invalid(es)
        case (FigletResult.Valid(a), FigletResult.Valid(b))         => FigletResult.Valid(f(a, b))
      }

    def tupled: FigletResult[(A, B)] =
      mapN((a, b) => (a, b))

  }

  /**
   * Syntax on 3-tuples of results, accumulating the errors of all sides
   */
  implicit class FigletResultTuple3Ops[A, B, C](
      private val tuple: (FigletResult[A], FigletResult[B], FigletResult[C]),
  ) extends AnyVal {

    def mapN[D](f: (A, B, C) => D): FigletResult[D] =
      (tuple._1, tuple._2).tupled.flatMap {
        case (a, b) =>
          tuple._3.map(c => f(a, b, c))
      }

    def tupled: FigletResult[(A, B, C)] =
      mapN((a, b, c) => (a, b, c))

  }

  /**
   * Universal equality syntax
   */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit class FigletEqualsOps[A](private val a: A) extends AnyVal {

    def ===(b: A): Boolean =
      a == b

    def =!=(b: A): Boolean =
      a != b

  }

  /**
   * Syntax on 4-tuples of results, accumulating the errors of all sides
   */
  implicit class FigletResultTuple4Ops[A, B, C, D](
      private val tuple: (FigletResult[A], FigletResult[B], FigletResult[C], FigletResult[D]),
  ) extends AnyVal {

    def mapN[E](f: (A, B, C, D) => E): FigletResult[E] =
      (tuple._1, tuple._2, tuple._3).tupled.flatMap {
        case (a, b, c) =>
          tuple._4.map(d => f(a, b, c, d))
      }

    def tupled: FigletResult[(A, B, C, D)] =
      mapN((a, b, c, d) => (a, b, c, d))

  }

}
