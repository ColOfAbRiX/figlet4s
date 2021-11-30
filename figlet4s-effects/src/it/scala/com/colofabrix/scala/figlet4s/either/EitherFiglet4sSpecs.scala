package com.colofabrix.scala.figlet4s.either

import cats.scalatest._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.StandardTestData._
import com.colofabrix.scala.figlet4s.testutils.Figlet4sMatchers
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class EitherFiglet4sSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with EitherMatchers {

  "Rendering APIs" should "render a default text using the \"standard\" font" in {
    val computed =
      for {
        options <- standardBuilder.options
      } yield {
        val computed = Figlet4s.renderString(standardInput, options)
        val expected = FIGure(options.font, standardInput, Vector(standardLines.toSubcolumns))
        computed should lookLike(expected)
      }

    computed should be(right)
  }

}
