package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.Utils._
import Math._
import scala.annotation.tailrec
import scala.util.matching.Regex

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class HorizontalFittingRenderer extends HorizontalTextRenderer[HorizontalFittingLayout.type] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure = {
    val result = merge(first.lastColumns.value, second.lastColumns.value, 0, Vector.empty)
    first.replace(
      second.copy(
        lines = Vector(SubColumns(result).toSublines)
      )
    )
  }

  @tailrec
  private def merge(a: Vector[String], b: Vector[String], overlap: Int, partial: Vector[String]): Vector[String] =
    if (a.length === 0) b
    else if (b.length === 0) a
    else {
      val (left, aOverlap)  = a.splitAt(a.length - overlap)
      val (bOverlap, right) = b.splitAt(overlap)

      val testMerge =
        (aOverlap zip bOverlap)
          .traverse {
            case (aOverlapColumn, bOverlapColumn) =>
              (aOverlapColumn zip bOverlapColumn)
                .traverse {
                  case (' ', ' ')  => Some(' ')
                  case (aStr, ' ') => Some(aStr)
                  case (' ', bStr) => Some(bStr)
                  case (_, _)      => None
                }
                .map(_.mkString)
          }
          .map(left ++ _ ++ right)

      testMerge match {
        case None        => partial
        case Some(value) => merge(a, b, overlap + 1, value)
      }
    }
}

object HorizontalFittingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new HorizontalFittingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new HorizontalFittingRenderer().render(text, font)
}
