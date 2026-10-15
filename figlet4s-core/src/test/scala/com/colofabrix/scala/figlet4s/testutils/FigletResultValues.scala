package com.colofabrix.scala.figlet4s.testutils

import com.colofabrix.scala.figlet4s.FigletResult
import org.scalatest.matchers._

/**
 * ScalaTest support for FigletResult, replacing the matchers previously provided by cats-scalatest
 */
object FigletResultValues {

  /**
   * Matcher for valid results
   */
  val valid: BeMatcher[FigletResult[?]] =
    new BeMatcher[FigletResult[?]] {
      def apply(left: FigletResult[?]): MatchResult =
        MatchResult(
          left.isValid,
          s"$left was not a valid result",
          s"$left was a valid result",
        )
    }

  /**
   * Matcher for invalid results
   */
  val invalid: BeMatcher[FigletResult[?]] =
    new BeMatcher[FigletResult[?]] {
      def apply(left: FigletResult[?]): MatchResult =
        MatchResult(
          left.isInvalid,
          s"$left was not an invalid result",
          s"$left was an invalid result",
        )
    }

  /**
   * Matcher for a valid result carrying the given value
   */
  def beValid[A](expected: A): Matcher[FigletResult[A]] =
    (result: FigletResult[A]) =>
      MatchResult(
        result.isValid && result.unsafeGet == expected,
        s"FigletResult $result was not valid with value $expected",
        s"FigletResult was valid with value $expected",
      )

  /**
   * Matcher for an invalid result containing an error with the given message
   */
  def haveInvalid(message: String): Matcher[FigletResult[?]] =
    (result: FigletResult[?]) => {
      val found = result.getErrors.map(_.getMessage).contains(message)
      MatchResult(
        found,
        s"FigletResult $result did not contain an error with message '$message'",
        s"FigletResult contained an error with message '$message'",
      )
    }

  /**
   * Accessors for the content of a FigletResult
   */
  implicit class FigletResultValuesOps[A](private val result: FigletResult[A]) extends AnyVal {

    /** The valid value, or throws the first error */
    def value: A =
      result.fold(e => throw e.head, identity)

    /** The messages of the errors, or an empty list when the result is valid */
    def invalidValue: List[String] =
      result.getErrors.map(_.getMessage)

  }

}
