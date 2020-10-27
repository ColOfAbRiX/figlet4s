package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

  "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
    val text = "P%J"

    val builder =
      Figlet4s
        .builder(text)
        .withInternalFont("whimsy")
        .withHorizontalLayout(HorizontalLayout.HorizontalFitting)

    val computed = builder.render()
    val expected = renderWithFiglet(builder.options, text)

    computed should lookLike(expected)
  }

// NOTE: More to test
//  - TestRenderOptions(P%J,whimsy,HorizontalFitting,LeftToRight,FlushLeft)
//  - TestRenderOptions(j  k,cricket,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions(x|,bigchief,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions((|*,eftiwall,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions(P{L,crawford,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions(s@,,serifcap,FontDefault,LeftToRight,FlushLeft)

}
