package com.colofabrix.scala.figlet4s.renderes

import cats.kernel.Monoid
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.figfont.FIGure._

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class FullWidthRenderer(val font: FIGfont, options: RenderOptions)
    extends HorizontalTextRenderer[FullWidthHorizontalLayout.type] { self =>

  implicit protected val M: Monoid[FIGure] = new Monoid[FIGure] {
    def empty: FIGure                         = self.empty
    def combine(x: FIGure, y: FIGure): FIGure = self.append(x, y)
  }

  /**
   * Creates an empty FIGure of the currently used FIGfont
   */
  def empty: FIGure = FIGure(font)

  /**
   * Appends two FIGures using the rule of the current layout
   */
  def append(x: FIGure, y: FIGure): FIGure = {
    val appended = appendInline(x, y)

    // FIXME: This doesn't work
    val lastFigline =
      if (appended.last.size <= options.maxWidth) appended
      else appendNewLine(x, y)

    x.copy(lines = x.lines.init ++ lastFigline, value = x.value + y.value)
  }

  private def appendInline(first: FIGure, second: FIGure): Vector[FIGline] = {
    val appended =
      (first.lines.last zip removeHardblanks(second.lines.last))
        .map { case (f, s) => f + s }

    Vector(appended)
  }

  private def appendNewLine(first: FIGure, second: FIGure): Vector[FIGline] =
    first.lines.last +: Vector(removeHardblanks(second.lines.last))

  private def removeHardblanks(line: FIGline): FIGline =
    line.map(_.replace(font.header.hardblank.toString, ""))
}

object FullWidthRenderer {
  def apply(font: FIGfont, options: RenderOptions = RenderOptions()) =
    new FullWidthRenderer(font, options)
}
