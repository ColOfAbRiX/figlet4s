package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

  "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
    val text = "w~5"

    val builder =
      Figlet4s
        .builder(text)
        .withInternalFont("trek")
        .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)

    val computed = builder.render()
    val expected = renderWithFiglet(builder.options, text)

    computed should lookLike(expected)
    computed.print()
    expected.print()
  }

// NOTE: More to test
//  - TestRenderOptions(j  k,cricket,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions(uw~5v>q5uZ,trek,HorizontalSmushing,LeftToRight,FlushLeft)
}
