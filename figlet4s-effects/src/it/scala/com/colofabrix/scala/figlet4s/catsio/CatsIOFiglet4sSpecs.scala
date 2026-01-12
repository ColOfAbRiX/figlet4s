package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import cats.effect.unsafe.implicits.global
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.StandardTestData._
import com.colofabrix.scala.figlet4s.testutils.Figlet4sMatchers
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class CatsIOFiglet4sSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  "Rendering APIs" should "render a default text using the \"standard\" font" in {
    val test =
      for {
        options <- standardBuilder.options
      } yield {
        val computed = Figlet4s.renderString(standardInput, options)
        val expected = FIGure(options.font, standardInput, Vector(standardLines.toSubcolumns))
        computed should lookLike(expected)
      }

    run(test)
  }

}
