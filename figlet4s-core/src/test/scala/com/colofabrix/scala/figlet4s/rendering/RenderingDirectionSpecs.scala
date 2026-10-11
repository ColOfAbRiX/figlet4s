package com.colofabrix.scala.figlet4s.rendering

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

/**
 * Golden tests rendering "Hubert" with the standard FIGfont, as the original command line figlet does
 */
class RenderingDirectionSpecs extends AnyFlatSpec with Matchers {

  //  Data setup  //

  private val font = Figlet4s.loadFontInternal()
  private val text = "Hubert"

  private val ltrExpected =
    Vector(
      "_   _       _               _",
      "| | | |_   _| |__   ___ _ __| |_",
      "| |_| | | | | '_ \\ / _ \\ '__| __|",
      "|  _  | |_| | |_) |  __/ |  | |_",
      "|_| |_|\\__,_|_.__/ \\___|_|   \\__|",
      "",
    ).map(_.trim)

  private val rtlExpected =
    Vector(
      "_            _           _   _",
      "| |_ _ __ ___| |__  _   _| | | |",
      "| __| '__/ _ \\ '_ \\| | | | |_| |",
      "| |_| | |  __/ |_) | |_| |  _  |",
      "\\__|_|  \\___|_.__/ \\__,_|_| |_|",
      "",
    ).map(_.trim)

  private def options(printDirection: PrintDirection): RenderOptions =
    RenderOptions(
      font,
      80,
      HorizontalLayout.FontDefault,
      printDirection,
      Justification.FontDefault,
    )

  private def renderedLines(figure: FIGure): Vector[String] =
    figure.cleanLines.flatMap(_.value.map(_.trim)).toVector

  //  Left-to-right rendering  //

  "Rendering with left-to-right print direction" should "match the original figlet output" in {
    val figure = Figlet4s.renderString(text, options(PrintDirection.LeftToRight))

    renderedLines(figure) shouldBe ltrExpected
  }

  //  Right-to-left rendering  //

  "Rendering with right-to-left print direction" should "match the original figlet output" in {
    val figure = Figlet4s.renderString(text, options(PrintDirection.RightToLeft))

    renderedLines(figure) shouldBe rtlExpected
  }

  it should "default the justification to flush-right" in {
    val figure = Figlet4s.renderString(text, options(PrintDirection.RightToLeft))

    figure.lines.head.value.map(_.length) should contain only 79
  }

  //  Font default direction  //

  "Rendering with font default print direction" should "use the direction declared in the FIGfont header" in {
    val figure = Figlet4s.renderString(text, options(PrintDirection.FontDefault))

    renderedLines(figure) shouldBe ltrExpected
  }

}
