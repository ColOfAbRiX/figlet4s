package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.header._

/**
 * FIGlet Font
 */
case class FIGfont(header: FlfHeader, comment: String)

object FIGfont {
  private case class BuilderState(
      header: Option[FlfHeader] = None,
      commentLines: Vector[String] = Vector.empty,
  )

  def fromLines(lines: Vector[String]): FigletResult[FIGfont] = {
    val builderState = lines
      .zipWithIndex
      .foldLeft(BuilderState().validNec[FigletError]) {
        case (stateV, (line, index)) =>
          stateV andThen { state =>
            parseLine(state, line, index)
          }
      }

    builderState andThen { state =>
      val header  = state.header.get
      val comment = state.commentLines.mkString("\n")
      FIGfont(header, comment).validNec
    }
  }

  private def parseLine(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    val newState: FigletResult[BuilderState] = if (index == 0) {
      FlfHeader.fromString(line) andThen { header =>
        state.copy(header = Some(header)).validNec
      }

    } else if (index > 0 && index < state.header.get.commentLines) {
      state.copy(commentLines = state.commentLines :+ line).validNec

    } else state.validNec

    newState
  }
}
