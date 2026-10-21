package com.colofabrix.scala.figlet4s.unsafe

import cats.effect._
import cats.effect.unsafe.implicits.global
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

/** Regression test for the nvscript right-to-left rendering, which the original figlet 2.2.5 renders
  * incorrectly: it wraps texts near the output-width boundary and replaces rows with blank lines or
  * garbles them. The comparison ignores the blank rows figlet spuriously emits.
  */
class UnsafeFiglet4sRegressionSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with OriginalFigletTesting {

  private val brokenText      = "&* aOqO,w#RgZF*OX-R%"
  private val brokenFont      = "contributed/nvscript"
  private val brokenDirection = PrintDirection.RightToLeft
  private val brokenJustify   = Justification.FlushRight

  "Rendering APIs" should "render the known problematic input as the original FIGlet does" taggedAs (SlowTest) in {
    assumeExecutableInPath("figlet")

    val font     = cachedFont(brokenFont)
    val maxWidth = font.header.maxLength * brokenText.length()

    val builder =
      Figlet4s
        .builder()
        .text(brokenText)
        .withMaxWidth(maxWidth)
        .withFont(font)
        .withPrintDirection(brokenDirection)
        .withJustification(brokenJustify)

    val computed = IO(builder.render())
    val expected = renderWithFiglet(builder.options, brokenText)

    IO
      .both(computed, expected)
      .map { case (c, e) =>
        val expectedLines = e.lines.flatMap(_.value).filterNot(_.trim.isEmpty)
        c.cleanLines.flatMap(_.value) should contain theSameElementsInOrderAs expectedLines
      }
      .unsafeRunSync()
  }

}
