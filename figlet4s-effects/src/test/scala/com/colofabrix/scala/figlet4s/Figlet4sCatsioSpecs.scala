package com.colofabrix.scala.figlet4s

import cats.effect._
import com.colofabrix.scala.figlet4s.catsio._
import com.colofabrix.scala.figlet4s.errors._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class Figlet4sCatsioSpecs extends AnyFlatSpec with Matchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  "figlet4s-catsio" should "return an error inside IO" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.withFont("non_existent").render()
      result  <- figure.asStringF()
    } yield result

    assertThrows[FigletLoadingError] {
      run(result)
    }
  }

}
