package com.colofabrix.scala.figlet4s

import cats.effect._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.catsio._
import com.colofabrix.scala.figlet4s.errors._

class Figlet4sCatsioSpecs extends AnyFlatSpec with Matchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  private val expected =
    """  /\/|         _____              ___      ____   _ ____  _____ """ + "\n" +
    """ |/\/  __/\__ |  ___|_ _  ___    ( _ )    / ___| / |___ \|___ / """ + "\n" +
    """       \    / | |_ / _` |/ _ \   / _ \/\ | |     | | __) | |_ \ """ + "\n" +
    """       /_  _\ |  _| (_| | (_) | | (_>  < | |___  | |/ __/ ___) |""" + "\n" +
    """         \/   |_|  \__,_|\___/   \___/\/  \____| |_|_____|____/ """ + "\n" +
    """                                                                """

  "figlet4s-catsio" should "render a test case" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.render()
      result  <- figure.asString()
    } yield result

    run(result) should equal(expected)
  }

  it should "return an error inside IO" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.withFont("non_existent").render()
      result  <- figure.asString()
    } yield result

    assertThrows[FigletLoadingError] {
      run(result)
    }
  }

  //it should "return a list with the internal fonts" in {
  //  Figlet4s.internalFonts should contain("standard")
  //}
}
