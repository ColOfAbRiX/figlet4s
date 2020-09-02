package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import org.scalatest.matchers._

trait Figlet4sMatchers {
  class FIGureMatchers(expected: FIGure) extends Matcher[FIGure] {
    def apply(computed: FIGure) = {
      MatchResult(
        computed.columns == expected.columns,
        "Expected result is NOT equal to the computed result",
        "Expected result is equal to the computed result",
      )
    }
  }

  def similar(expected: FIGure) = new FIGureMatchers(expected)
}

// Make them easy to import with:
// import FIGureMatchers._
object Figlet4sMatchers extends Figlet4sMatchers
