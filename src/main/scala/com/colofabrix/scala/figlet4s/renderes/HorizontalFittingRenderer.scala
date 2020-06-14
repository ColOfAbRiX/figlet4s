package com.colofabrix.scala.figlet4s.renderes

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import scala.util.matching.Regex

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class HorizontalFittingRenderer extends HorizontalTextRenderer[HorizontalFittingLayout.type] {
  private val fullRightTrimRE: Regex = " +$".r
  private val fullLeftTrimRE: Regex  = "^ +".r

  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure = {
    val zipped = (first.lastLine zip second.lastLine)

    val trim = zipped
      .foldLeft(Int.MaxValue) {
        case (maxTrim, (fLine, sLine)) =>
          val fSpaces = fLine.length - fullRightTrimRE.replaceAllIn(fLine, "").length
          val sSpaces = sLine.length - fullLeftTrimRE.replaceAllIn(sLine, "").length
          val spaces  = fSpaces + sSpaces
          Math.min(spaces, maxTrim)
      }

    val rightTrimRE = s" {0,$trim}$$".r
    val leftTrimRE  = List.tabulate(trim + 1)(i => s"^ {0,$i}".r)

    val appended = zipped
      .map {
        case (fLine, sLine) =>
          val fTrimmed = rightTrimRE.replaceAllIn(fLine, "")
          val sTrim    = trim - (fLine.length - fTrimmed.length)
          fTrimmed + leftTrimRE(sTrim).replaceAllIn(sLine, "")
      }

    first.copy(
      lines = first.lines.init ++ Vector(appended),
      value = first.value + second.value,
    )
  }
}

object HorizontalFittingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new HorizontalFittingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new HorizontalFittingRenderer().render(text, font)
}
