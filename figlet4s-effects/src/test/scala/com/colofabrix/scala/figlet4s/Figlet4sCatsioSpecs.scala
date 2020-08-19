package com.colofabrix.scala.figlet4s

import org.scalatest.flatspec._
import org.scalatest.matchers.should
import com.colofabrix.scala.figlet4s.catsio._

class Figlet4sCatsioSpecs extends AnyFlatSpec with should.Matchers {

  "figlet4s-catsio" should "render a test case" in {
    val program = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.render()
      _       <- figure.print()
    } yield ()

    program.unsafeRunSync()
  }

}
