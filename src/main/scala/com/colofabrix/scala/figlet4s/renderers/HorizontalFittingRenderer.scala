package com.colofabrix.scala.figlet4s.renderers

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
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
    val result = merge(first.lastColumns.value, second.lastColumns.value, 0, 0)
    first.replace(second.copy(lines = Vector(SubColumns(result).toSublines)))
  }

  @tailrec
  private def merge(a: Vector[String], b: Vector[String], aOffset: Int, bOffset: Int): Vector[String] = {
    if (aOffset === a.length - 1 || a.length === 0)
      b
    else if (bOffset === b.length - 1 || b.length === 0)
      a
    else {
      // Calculate if the offset of A or B can be moved
      val (aMove, bMove) =
        (a(a.length - aOffset - 1) zip b(bOffset))
          .foldLeft((0, 0)) {
            case (result, (' ', ' ')) => (max(result._1, 1), max(result._2, 1))
            case (result, (_, ' '))   => (max(result._1, 0), max(result._2, 1))
            case (result, (' ', _))   => (max(result._1, 1), max(result._2, 0))
            case (result, (_, _))     => (max(result._1, 0), max(result._2, 0))
          }

      if (aMove =!= 0 && bMove =!= 0)
        // If one of the offsets can be moved, keep moving
        merge(a, b, aOffset + aMove, bOffset + bMove)
      else {
        // Offset can't be moved further, merge what overlaps
        val (left, aOverlap)  = a.splitAt(aOffset)
        val (bOverlap, right) = b.splitAt(bOffset)
        val merged            =
          (aOverlap zip bOverlap)
            .map {
              case (" ", " ")  => " "
              case (aStr, " ") => aStr
              case (" ", bStr) => bStr
              case (_, _)      => "!"
            }
        left ++ merged ++ right
      }
    }
  }
}

object HorizontalFittingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new HorizontalFittingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new HorizontalFittingRenderer().render(text, font)
}
