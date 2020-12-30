package com.colofabrix.scala.figlet4s.rendering

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.{ Justification => ClientJustification, RenderOptions }

/**
 * Merging rules for horizontal appending of characters
 */
private[figlet4s] object Alignment {

  def applyAlignment(options: RenderOptions, outputLines: Seq[SubColumns]): Seq[SubColumns] =
    outputLines.map { columns =>
      val lines = columns.toSublines
      val flushed =
        options.justification match {
          case ClientJustification.FlushLeft   => alignLeft(lines)
          case ClientJustification.Center      => alignCenter(lines, options.maxWidth)
          case ClientJustification.FlushRight  => alignRight(lines, options.maxWidth)
          case ClientJustification.FontDefault => lines
        }
      flushed.toSubcolumns
    }

  /**
   * Align SubLines at the left of the given width
   */
  private def alignLeft(input: SubLines): SubLines =
    input

  /**
   * Align SubLines in the center of the given width
   */
  private def alignCenter(input: SubLines, maxWidth: Int): SubLines = {
    val halfSpaces   = (maxWidth - input.width) / 2.0
    val leftPadding  = " " * Math.ceil(halfSpaces).toInt
    val rightPadding = " " * Math.floor(halfSpaces).toInt
    input.map(leftPadding + _ + rightPadding)
  }

  /**
   * Align SubLines at the right of the given width
   */
  private def alignRight(input: SubLines, maxWidth: Int): SubLines = {
    val leftPadding = " " * (maxWidth - input.width - 1)
    input.map(leftPadding + _)
  }

}
