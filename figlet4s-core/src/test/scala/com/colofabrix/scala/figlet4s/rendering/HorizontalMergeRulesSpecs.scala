package com.colofabrix.scala.figlet4s.rendering

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import com.colofabrix.scala.figlet4s.rendering.Rendering._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class HorizontalMergeRulesSpecs extends AnyFlatSpec with Matchers {

  //  Data setup  //

  private val universalFont = Figlet4s.loadFontInternal("cricket")
  private val hardblank     = universalFont.header.hardblank
  private val smushable     = MergeState(1, 5, 5)
  private val tooNarrow     = MergeState(1, 1, 5)

  //  Universal Smushing  //

  "Universal smushing" should "give precedence to the right character when printing left-to-right" in {
    val computed = strategy(PrintDirection.LeftToRight)(smushable)('a', 'b')
    val expected = CurrentLast('b')

    computed shouldBe expected
  }

  it should "give precedence to the left character when printing right-to-left" in {
    val computed = strategy(PrintDirection.RightToLeft)(smushable)('a', 'b')
    val expected = CurrentLast('a')

    computed shouldBe expected
  }

  it should "give precedence to visible characters over hardblanks in both directions" in {
    val ltrStrategy = strategy(PrintDirection.LeftToRight)
    val rtlStrategy = strategy(PrintDirection.RightToLeft)

    ltrStrategy(smushable)(hardblank, 'b') shouldBe CurrentLast('b')
    ltrStrategy(smushable)('b', hardblank) shouldBe CurrentLast('b')
    rtlStrategy(smushable)(hardblank, 'b') shouldBe CurrentLast('b')
    rtlStrategy(smushable)('b', hardblank) shouldBe CurrentLast('b')
  }

  it should "never smush two hardblanks" in {
    val computed = strategy(PrintDirection.RightToLeft)(smushable)(hardblank, hardblank)

    computed shouldBe Stop
  }

  it should "smush blanks in both directions" in {
    val ltrStrategy = strategy(PrintDirection.LeftToRight)
    val rtlStrategy = strategy(PrintDirection.RightToLeft)

    ltrStrategy(smushable)('a', ' ') shouldBe Continue('a')
    ltrStrategy(smushable)(' ', 'b') shouldBe Continue('b')
    rtlStrategy(smushable)('a', ' ') shouldBe Continue('a')
    rtlStrategy(smushable)(' ', 'b') shouldBe Continue('b')
  }

  it should "not smush when one of the characters is too narrow" in {
    val computed = strategy(PrintDirection.RightToLeft)(tooNarrow)('a', 'b')

    computed shouldBe Stop
  }

  //  Controlled Smushing  //

  "Controlled smushing" should "apply the rules spatially regardless of the print direction" in {
    val ltrStrategy = strategy(PrintDirection.LeftToRight, Figlet4s.loadFontInternal())
    val rtlStrategy = strategy(PrintDirection.RightToLeft, Figlet4s.loadFontInternal())

    ltrStrategy(smushable)('[', ']') shouldBe CurrentLast('|')
    rtlStrategy(smushable)('[', ']') shouldBe CurrentLast('|')
    ltrStrategy(smushable)('/', '\\') shouldBe Stop
    rtlStrategy(smushable)('/', '\\') shouldBe Stop
  }

  private def strategy(direction: PrintDirection, font: FIGfont = universalFont): MergeStrategy =
    HorizontalMergeRules.mergeStrategy(
      RenderOptions(font, 80, HorizontalLayout.FontDefault, direction, Justification.FontDefault),
    )

}
