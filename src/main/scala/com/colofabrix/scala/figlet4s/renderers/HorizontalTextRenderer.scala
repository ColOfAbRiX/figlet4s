package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Renders a String for the horizontal direction with the specified FIGfont
 */
trait HorizontalTextRenderer[A <: HorizontalLayout] {
  /**
   * Renders a String into a FIGure
   */
  def render(text: String, font: FIGfont): FIGure =
    render(text, font, RenderOptions())

  /**
   * Renders a String into a FIGure
   */
  def render(text: String, font: FIGfont, opts: RenderOptions): FIGure = {
    val result = text
      .map(font(_).columns)
      .foldLeft(Vector(empty(font)))(appendChar(opts))
    FIGure(font, text, result)
  }

  private def appendChar(opts: RenderOptions)(accumulator: Vector[SubColumns], column: SubColumns): Vector[SubColumns] =
    accumulator
      .lastOption
      .map { lastLine =>
        accumulator.drop(1) :+ appendColumns(lastLine, column)
      }
      .filter(_.length <= opts.maxWidth.getOrElse(Int.MaxValue))
      .getOrElse(accumulator :+ column)

  /**
   * Appends SubColumns of two FIGures
   */
  protected def appendColumns(first: SubColumns, second: SubColumns): SubColumns

  private def empty(font: FIGfont): SubColumns =
    font.zero.lines.toSubcolumns
}
