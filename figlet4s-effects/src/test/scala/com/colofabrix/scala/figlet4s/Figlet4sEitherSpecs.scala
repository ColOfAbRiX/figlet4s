package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.either._
import org.scalatest.flatspec._
import org.scalatest.matchers.should

class Figlet4sEitherSpecs extends AnyFlatSpec with should.Matchers {

  "figlet4s-either" should "render a test case" in {
    for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.render()
      _       <- figure.print()
    } yield ()
  }

}
