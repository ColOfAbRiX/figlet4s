package com.colofabrix.scala.figlet4s

import cats.effect._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.catsio._

class Figlet4sCatsioSpecs extends AnyFlatSpec with Matchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  "figlet4s-catsio" should "render a test case" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.render()
      result  <- figure.asVector()
    } yield result

    println(run(result))
  }

  it should "return an error inside IO" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.withFont("non_existent").render()
      result  <- figure.asVector()
    } yield result

    println(run(result))
  }

}
