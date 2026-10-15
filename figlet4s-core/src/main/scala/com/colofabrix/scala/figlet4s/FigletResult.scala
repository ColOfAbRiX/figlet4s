package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.errors._
import scala.annotation.tailrec
import scala.util.Try

/**
 * The outcome of an operation that might result in one or more errors
 */
@SuppressWarnings(Array("org.wartremover.warts.IterableOps", "org.wartremover.warts.Throw"))
sealed trait FigletResult[+A] {

  /**
   * Applies one of the two given functions, depending on the outcome
   */
  def fold[C](onError: List[FigletException] => C, onValid: A => C): C =
    this match {
      case FigletResult.Invalid(errors) => onError(errors)
      case FigletResult.Valid(value)    => onValid(value)
    }

  /**
   * Transforms the valid value, keeping any error
   */
  def map[B](f: A => B): FigletResult[B] =
    fold(FigletResult.Invalid(_): FigletResult[B], value => FigletResult.Valid(f(value)))

  /**
   * Chains a computation on the valid value, stopping at the first error
   */
  def flatMap[B](f: A => FigletResult[B]): FigletResult[B] =
    fold(FigletResult.Invalid(_), f)

  /**
   * Alias of flatMap
   */
  def andThen[B](f: A => FigletResult[B]): FigletResult[B] =
    flatMap(f)

  /**
   * Transforms the errors, keeping the valid value
   */
  def mapErrors(f: FigletException => FigletException): FigletResult[A] =
    fold(errors => FigletResult.Invalid(errors.map(f)): FigletResult[A], FigletResult.Valid(_))

  /**
   * The valid value or the given default
   */
  def getOrElse[AA >: A](default: => AA): AA =
    fold(_ => default, identity)

  /**
   * The valid value or throws the first error
   */
  @throws(classOf[FigletException])
  def unsafeGet: A =
    fold(errors => throw errors.head, identity)

  /**
   * The outcome as an Either, capturing the first error on the left side
   */
  def toEither: Either[FigletException, A] =
    fold(errors => Left(errors.head), Right(_))

  /**
   * True when the result contains a valid value
   */
  def isValid: Boolean =
    fold(_ => false, _ => true)

  /**
   * True when the result contains errors
   */
  def isInvalid: Boolean =
    !isValid

  /**
   * The list of errors, or an empty list when the result is valid
   */
  def getErrors: List[FigletException] =
    fold(identity, _ => Nil)

}

object FigletResult {

  /**
   * A valid outcome holding a value
   */
  final case class Valid[+A](value: A) extends FigletResult[A]

  /**
   * An invalid outcome holding at least one error. Construction is restricted to preserve the non-empty guarantee.
   */
  final case class Invalid private[figlet4s] (errors: List[FigletException]) extends FigletResult[Nothing]

  /**
   * A valid outcome holding the given value
   */
  def apply[A](value: A): FigletResult[A] =
    Valid(value)

  /**
   * An invalid outcome holding the given errors
   */
  def fail(error: FigletException, more: FigletException*): FigletResult[Nothing] =
    Invalid((error :: more.toList).reverse)

  /**
   * A valid outcome when the condition holds, otherwise an invalid outcome with the given error
   */
  def ensure[A](cond: Boolean, value: => A, error: => FigletException): FigletResult[A] =
    if (cond) Valid(value) else Invalid(error :: Nil)

  /**
   * A valid outcome when the Try succeeds, otherwise an invalid outcome with the mapped error
   */
  def fromTry[A](value: Try[A])(mapError: Throwable => FigletException): FigletResult[A] =
    value match {
      case scala.util.Success(a) => Valid(a)
      case scala.util.Failure(e) => Invalid(mapError(e) :: Nil)
    }

  /**
   * Combines a sequence of results into one result holding all the values or all the errors
   */
  def sequence[A](results: Seq[FigletResult[A]]): FigletResult[Seq[A]] = {
    @tailrec
    def loop(remaining: List[FigletResult[A]], values: List[A], errors: List[FigletException]): FigletResult[Seq[A]] =
      remaining match {
        case Nil =>
          if (errors.isEmpty) Valid(values.reverse) else Invalid(errors.reverse)
        case Valid(value) :: tail =>
          loop(tail, value :: values, errors)
        case Invalid(es) :: tail =>
          loop(tail, values, es.reverse ::: errors)
      }
    loop(results.toList, Nil, Nil)
  }

}
