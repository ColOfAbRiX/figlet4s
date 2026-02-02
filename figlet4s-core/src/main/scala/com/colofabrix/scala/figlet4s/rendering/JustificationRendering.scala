package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

final private[rendering] class JustificationRendering private (options: RenderOptions) {

  /**
   * Applies justification to a single line of SubColumns
   */
  def applyJustification(line: SubColumns): SubColumns = {
    val lineWidth     = line.value.length
    val paddingNeeded = Math.max(0, options.maxWidth - lineWidth)

    if (paddingNeeded === 0) {
      line
    } else {
      val resolvedJustification = resolveJustification(options.justification)
      val paddingColumn         = " " * line.height

      resolvedJustification match {
        case Justification.FontDefault =>
          line
        case Justification.FlushLeft =>
          line
        case Justification.FlushRight =>
          val adjustedPadding = Math.max(0, paddingNeeded - 1)
          val padding         = Vector.fill(adjustedPadding)(paddingColumn)
          SubColumns(padding ++ line.value)
        case Justification.Center =>
          val leftPadding = paddingNeeded / 2
          val padding     = Vector.fill(leftPadding)(paddingColumn)
          SubColumns(padding ++ line.value)
      }
    }
  }

  /** Resolves FontDefault justification based on print direction */
  private def resolveJustification(justification: Justification): Justification =
    justification match {
      case Justification.FontDefault =>
        val fontPrintDirection = options.font.settings.printDirection

        val effectiveDirection =
          options.printDirection match {
            case PrintDirection.FontDefault => fontPrintDirection
            case PrintDirection.LeftToRight => FIGfontParameters.PrintDirection.LeftToRight
            case PrintDirection.RightToLeft => FIGfontParameters.PrintDirection.RightToLeft
          }

        effectiveDirection match {
          case FIGfontParameters.PrintDirection.LeftToRight => Justification.FlushLeft
          case FIGfontParameters.PrintDirection.RightToLeft => Justification.FlushRight
        }
      case other =>
        other
    }

}

object JustificationRendering {

  def apply(options: RenderOptions): JustificationRendering =
    new JustificationRendering(options)

}
