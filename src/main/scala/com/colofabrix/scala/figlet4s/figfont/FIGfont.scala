package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.header._
import cats.data.Validated._

/**
 * FIGlet Font
 */
case class FIGfont(header: FlfHeader, comment: String, characters: Map[Char, FIGcharacter])

object FIGfont {
  /** Used to keep track of the state while scanning the input */
  private case class BuilderState(
      header: Option[FlfHeader] = None,
      commentLines: Vector[String] = Vector.empty,
      loadedChars: Map[Char, FIGcharacter] = Map.empty,
      loadedCharLines: Vector[String] = Vector.empty,
  )

  /**
   * Creates a new FIGfont by parsing an input vector of lines
   */
  def apply(lines: Vector[String]): FigletResult[FIGfont] = {
    // Parse line by line and fill the state used to build the font
    val builderState = lines
      .zipWithIndex
      .foldLeft(BuilderState().validNec[FigletError]) {
        case (i @ Invalid(_), _)           => i
        case (Valid(state), (line, index)) => processLine(state, line, index)
      }

    // Create the font from the builder state
    builderState andThen { state =>
      val header     = state.header.get
      val comment    = state.commentLines.mkString("\n")
      val characters = state.loadedChars
      FIGfont(header, comment, characters).validNec
    }
  }

  private def processLine(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    if (index == 0) {
      // Parsing of the FLF header
      FlfHeader(line) andThen { header =>
        state.copy(header = Some(header)).validNec
      }

    } else {
      // The header is always present when we arrive here otherwise the step above would fail
      val header = state.header.get

      // println(s"\nIndex: $index")
      // println(s"Line: *$line*")
      // print("loadedCharLines ")
      // println(state.loadedCharLines)
      // print("loadedChars ")
      // println(state.loadedChars)

      if (index <= header.commentLines) {
        // Parsing of the comments
        state.copy(commentLines = state.commentLines :+ line).validNec

      } else if (state.loadedCharLines.length + 1 < header.height) {
        // Storing lines of a character
        println(s"Storing *$line*")
        state.copy(loadedCharLines = state.loadedCharLines :+ line).validNec

      } else {
        // Storing the new character
        val firstCharLine = 1 + header.commentLines
        val charIndex     = (index - firstCharLine - header.height) / header.height
        val charName      = (32 + charIndex).toChar
        val charValueV    = FIGcharacter(charName, state.loadedCharLines :+ line)

        charValueV andThen { charValue =>
          state
            .copy(
              loadedCharLines = Vector.empty,
              loadedChars = state.loadedChars + (charName -> charValue),
            ).validNec
        }
      }
    }
  }
}
