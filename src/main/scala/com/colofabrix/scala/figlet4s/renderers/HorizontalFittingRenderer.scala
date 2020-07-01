package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import scala.util.matching.Regex
import scala.annotation.tailrec

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class HorizontalFittingRenderer extends HorizontalTextRenderer[HorizontalFittingLayout.type] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure = {
    val result = append(first.lastColumns.value, second.lastColumns.value, Vector.empty[String])
    first.replace(second.copy(lines = Vector(SubColumns(result).toSublines)))
  }

  @tailrec
  private def append(first: Vector[String], second: Vector[String], result: Vector[String]): Vector[String] = {
    if (first.isEmpty)
      result ++ second
    else if (second.isEmpty)
      first ++ result
    else {
      val ((initFirst, Vector(lastFirst)), (Vector(headSecond), tailSecond)) =
        (first.splitAt(first.length - 1), second.splitAt(1))

      val columnMerge = mergeColumns(MergeColumn(initFirst, lastFirst, headSecond, tailSecond))
      val nextResult  = Vector(columnMerge.resultFirst, result, columnMerge.resultSecond).flatten

      append(columnMerge.unprocessedFirst, columnMerge.unprocessedSecond, nextResult)
    }
  }

  private case class MergeColumn(
      unprocessedFirst: Vector[String],
      lastColumnFirst: String,
      firstColumnSecond: String,
      unprocessedSecond: Vector[String],
  )

  private case class MergeResult(
      unprocessedFirst: Vector[String],
      unprocessedSecond: Vector[String],
      resultFirst: Vector[String],
      resultSecond: Vector[String],
  )

  private def mergeColumns(data: MergeColumn) = MergeResult(
    data.unprocessedFirst,
    data.unprocessedSecond,
    if (data.lastColumnFirst.trim.nonEmpty) Vector(data.lastColumnFirst) else Vector.empty,
    if (data.firstColumnSecond.trim.nonEmpty) Vector(data.firstColumnSecond) else Vector.empty,
  )
}

object HorizontalFittingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new HorizontalFittingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new HorizontalFittingRenderer().render(text, font)
}
