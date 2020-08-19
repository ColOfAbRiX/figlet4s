package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.either._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class Figlet4sEitherSpecs extends AnyFlatSpec with Matchers with EitherValues {

  "figlet4s-either" should "render a test case and return Right()" in {
    val result = Figlet4s.builder("~ * Fao & C 123").render()

    println(result)
    result.left.value match {
      case errors.FigletGenericException(message, inner) =>
        println(s"FigletGenericException($message, $inner)")
      case errors.FigletLoadingError(message, inner) =>
        println(s"FigletLoadingError($message, $inner)")
      case error =>
        println(s"Error: $error")
    }

    result.isRight should be(true)
  }

  it should "return an error inside Left()" in {
    val result =
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()

    result.isLeft should be(true)
  }

}
