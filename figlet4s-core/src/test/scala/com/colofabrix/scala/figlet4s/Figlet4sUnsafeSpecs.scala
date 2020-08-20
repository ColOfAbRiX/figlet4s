package com.colofabrix.scala.figlet4s

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.unsafe._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with Matchers {

  private val expected =
    """  /\/|         _____              ___      ____   _ ____  _____ """ + "\n" +
    """ |/\/  __/\__ |  ___|_ _  ___    ( _ )    / ___| / |___ \|___ / """ + "\n" +
    """       \    / | |_ / _` |/ _ \   / _ \/\ | |     | | __) | |_ \ """ + "\n" +
    """       /_  _\ |  _| (_| | (_) | | (_>  < | |___  | |/ __/ ___) |""" + "\n" +
    """         \/   |_|  \__,_|\___/   \___/\/  \____| |_|_____|____/ """ + "\n" +
    """                                                                """

  "figlet4s" should "render a test case" in {
    val result = Figlet4s
      .builder("~ * Fao & C 123")
      .render()
      .print()

    result should be(expected)
  }

  it should "throw an exception" in {
    assertThrows[Throwable] {
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .print()
    }
  }

}
