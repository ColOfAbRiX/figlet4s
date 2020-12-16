package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

  "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
    val text = "Fabrizio"

    val builder =
      Figlet4s
        .builder(text)
        .withHorizontalLayout(HorizontalLayout.FontDefault)
        .withPrintDirection(PrintDirection.RightToLeft)

    // println("FONT")
    // println(builder.options.font)

    println("COMPUTED:")
    val computed = builder.render()
    computed.print()

    println("EXPECTED:")
    val expected = renderWithFiglet(builder.options, text)
    expected.print()

    // computed should lookLike(expected)
  }

}
