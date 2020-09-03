package com.colofabrix.scala.figlet4s.testutils

import cats.implicits._
import cats.kernel._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.matchers._
import com.colofabrix.scala.figlet4s.options.RenderOptions

trait Figlet4sMatchers {
  implicit private val eqFoo: Eq[SubColumns] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  class FIGureMatchers(computed: FIGure) extends Matcher[FIGure] {
    def apply(expected: FIGure) = {
      val ec = expected.columns.head
      val cc = computed.columns.head

      def differences = for {
        (c, e) <- cc.zipAll(" " * cc.height, " " * ec.height)(ec)
        result <- compareColumns(c, e)
      } yield result

      def pritableDiffs =
        SubColumns(differences.toVector).toSublines.toString

      def diffMessage =
        s"The computed FIGure doesn't look like the expected FIGure. Here is a breakdown of the differences:\n\n" +
        s"Text: ${expected.value}\n" +
        s"Expected:\n${expected.asString()}\n\n" +
        s"Computed:\n${computed.asString()}\n\n" +
        s"Differences:\n$pritableDiffs\n"

      MatchResult(
        computed.columns === expected.columns,
        diffMessage,
        s"The computed FIGure looks like the expected FIGure.",
      )
    }

    private def compareColumns(a: String, b: String): Vector[String] =
      if (a =!= b) {
        val spacer = "+" + "|" * (a.length) + "+ "
        Vector(spacer, s"-$a-C", spacer, s"-$b-E", spacer)
      } else
        Vector(s"-$a- ")
  }

  def lookLike(computed: FIGure): FIGureMatchers =
    new FIGureMatchers(computed)

  def lookLike(options: RenderOptions, value: String, figure: Vector[SubColumns]): FIGureMatchers =
    lookLike(FIGure(options.font, value, figure))
}

object Figlet4sMatchers extends Figlet4sMatchers
