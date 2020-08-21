package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.either._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class Figlet4sEitherSpecs extends AnyFlatSpec with Matchers with EitherValues {

  private val expected =
    """  /\/|         _____              ___      ____   _ ____  _____ """ + "\n" +
    """ |/\/  __/\__ |  ___|_ _  ___    ( _ )    / ___| / |___ \|___ / """ + "\n" +
    """       \    / | |_ / _` |/ _ \   / _ \/\ | |     | | __) | |_ \ """ + "\n" +
    """       /_  _\ |  _| (_| | (_) | | (_>  < | |___  | |/ __/ ___) |""" + "\n" +
    """         \/   |_|  \__,_|\___/   \___/\/  \____| |_|_____|____/ """ + "\n" +
    """                                                                """

  "figlet4s-either" should "render a test case and return Right()" in {
    val result =
      Figlet4s
        .builder("~ * Fao & C 123")
        .render()
        .map(_.asString())

    result should be(Right(expected))
  }

  it should "return an error inside Left()" in {
    val result =
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .map(_.asString())

    result.isLeft should be(true)
  }

}
