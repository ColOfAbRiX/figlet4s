package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.either._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class Figlet4sEitherSpecs extends AnyFlatSpec with Matchers with EitherValues {

  "figlet4s-either" should "return an error inside Left()" in {
    val result =
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .map(_.asString())

    result.isLeft should be(true)
  }

}
