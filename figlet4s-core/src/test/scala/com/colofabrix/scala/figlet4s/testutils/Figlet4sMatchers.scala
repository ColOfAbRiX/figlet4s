package com.colofabrix.scala.figlet4s.testutils

import cats.implicits._
import cats.kernel._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.matchers._

/**
 * ScalaTest matchers for Figlet4s
 */
trait Figlet4sMatchers {
  implicit private val eqFoo: Eq[SubColumns] = Eq.fromUniversalEquals

  class FIGureMatchers(expected: FIGure) extends Matcher[FIGure] {
    def apply(computed: FIGure) = {
      val cc   = computed.cleanColumns.headOption.getOrElse(SubColumns(Vector.empty))
      val ec   = expected.cleanColumns.headOption.getOrElse(SubColumns(Vector.empty))
      val maxC = cc.value.map(_.length).maxOption.getOrElse(0)
      val maxE = ec.value.map(_.length).maxOption.getOrElse(0)

      def differences = for {
        ((c, e), i) <- cc.value.zip(ec.value).zipWithIndex
        result      <- compareColumns(i, c, e, maxC, maxE)
      } yield result

      def printableDiffs =
        SubColumns(differences.toVector).toSublines.toString

      def diffMessage =
        s"The expected FIGure doesn't look like the computed FIGure. Here is a breakdown of the differences:\n\n" +
        s"Text: '${computed.value}'\n\n" +
        s"Expected:\n${expected.asString()}\n\n" +
        s"Computed:\n${computed.asString()}\n\n" +
        s"Differences:\n$printableDiffs"

      MatchResult(
        expected.cleanColumns === computed.cleanColumns,
        diffMessage,
        s"The computed FIGure looks like the expected FIGure.",
      )
    }

    private def compareColumns(i: Int, e: String, c: String, maxE: Int, maxC: Int): Vector[String] = {
      val paddedC = c + " " * (maxC - c.length())
      val paddedE = e + " " * (maxE - e.length())

      if (e =!= c) {
        val List(n1, n2, n3) = "%3d".format(i).toList.map(_.toString)
        val spacer           = "+" + "|" * (e.length) + "+ "
        Vector(
          "|" + spacer,
          n1 + s"-$paddedC-E",
          n2 + spacer,
          n3 + s"-$paddedE-C",
          "|" + spacer,
        )
      } else Vector(s" -$paddedE- ")
    }

  }

  /**
   * Compares a FIGure with another FIGure to see if they look the same
   */
  def lookLike(expected: FIGure): FIGureMatchers =
    new FIGureMatchers(expected)
}

object Figlet4sMatchers extends Figlet4sMatchers
