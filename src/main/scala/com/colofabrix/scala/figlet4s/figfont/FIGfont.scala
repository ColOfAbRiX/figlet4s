package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._

/**
 * FIGlet Font
 */
final case class FIGfont(
    header: FlfHeader,
    comment: String,
    characters: Map[Char, FIGcharacter],
)

object FIGfont {
  private case class BuilderState(
      header: Option[FlfHeader] = None,
      commentLines: Vector[String] = Vector.empty,
      loadedChars: Map[Char, FIGcharacter] = Map.empty,
      loadedCharLines: Vector[String] = Vector.empty,
      processTaggedFonts: Boolean = false,
  )

  private val requiredChars = ((32 to 126) ++ Seq(196, 214, 220, 228, 246, 252, 223)).map(_.toChar)

  /**
   * Creates a new FIGfont by parsing an input vector of lines representing an FLF file
   */
  def apply(lines: Vector[String]): FigletResult[FIGfont] =
    lines
      .zipWithIndex
      .foldLeft(BuilderState().validNec[FigletError]) {
        case (i @ Invalid(_), _)           => i
        case (Valid(state), (line, index)) => processLine(state, line, index)
      }
      .andThen(parseBuilderState _)

  /**
   * Build the FIGfont by parsing the given state
   */
  private def parseBuilderState(state: BuilderState): FigletResult[FIGfont] =
    if (state.loadedCharLines.size != 0) {
      // Check we didn't stop in the middle of a character
      FlfCharacterError("Incomplete character definition").invalidNec

    } else if ((requiredChars.toSet diff state.loadedChars.keySet).size != 0) {
      // Check we loaded all required characters
      val missing = requiredChars.toSet diff state.loadedChars.keySet mkString (", ")
      FlfCharacterError(s"Missing definition for required FIGlet characters: $missing").invalidNec

    } else {
      // Build the font
      val header     = state.header.get
      val comment    = state.commentLines.mkString("\n")
      val characters = state.loadedChars
      FIGfont(header, comment, characters).validNec

    }

  /**
   * Processes a line calling the appropriate action based on the current state
   */
  private def processLine(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    if (index == 0)
      buildHeader(state, line)
    else if (index <= state.header.get.commentLines)
      buildComment(state, line)
    else if (!state.processTaggedFonts)
      buildCharacter(state, line, index)
    else
      buildTaggedCharacter(state, line, index)
  }

  /**
   * Parses the FLF header
   */
  private def buildHeader(state: BuilderState, line: String): FigletResult[BuilderState] =
    FlfHeader(line) andThen { header =>
      state.copy(header = Some(header)).validNec
    }

  /**
   * Builds the comment section
   */
  private def buildComment(state: BuilderState, line: String): FigletResult[BuilderState] =
    state.copy(commentLines = state.commentLines :+ line).validNec

  /**
   * Builds characters using the given state and the current line
   */
  private def buildCharacter(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    val header = state.header.get

    if (state.loadedCharLines.length + 1 < header.height) {
      state.copy(loadedCharLines = state.loadedCharLines :+ line).validNec

    } else {
      val startLine = index - state.loadedCharLines.size
      val charNum   = (startLine - header.commentLines - 1) / header.height

      FIGcharacter(header, requiredChars(charNum), state.loadedCharLines :+ line, None, startLine)
        .map { figChar =>
          val loadedChars  = state.loadedChars + (figChar.name -> figChar)
          val isNextTagged = loadedChars.size >= requiredChars.size
          state.copy(loadedCharLines = Vector.empty, loadedChars = loadedChars, processTaggedFonts = isNextTagged)
        }
    }
  }

  /**
   * Builds tagged characters using the given state and the current line
   */
  private def buildTaggedCharacter(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    val header = state.header.get

    if (state.loadedCharLines.length + 1 < header.height + 1) {
      state.copy(loadedCharLines = state.loadedCharLines :+ line).validNec

    } else {
      val startLine = index - state.loadedCharLines.size

      val splitFontTag = state.loadedCharLines.head.replaceFirst(" +", "###").split("###").toVector

      val nameV = Option
        .when(splitFontTag.size > 0)(splitFontTag(0))
        .toValidNec(FlfCharacterError(s"Missing character code at line ${index + 1}: $line"))
        .andThen(parseCharCode(startLine, _))

      val commentV = Option
        .when(splitFontTag.size > 1)(splitFontTag(1))
        .getOrElse("")
        .validNec[FigletError]
        .map(Option(_))

      val loadedCharLines = state.loadedCharLines.drop(1) :+ line

      (nameV, commentV)
        .mapN(FIGcharacter(header, _, loadedCharLines, _, startLine))
        .andThen(identity)
        .map { figChar =>
          val loadedChars = state.loadedChars + (figChar.name -> figChar)
          state.copy(loadedCharLines = Vector.empty, loadedChars = loadedChars)
        }
    }
  }

  /**
   * Parses a character code into a Char
   */
  private def parseCharCode(index: Int, code: String): FigletResult[Char] =
    if (code.matches("^-?\\d+$"))
      Integer.parseInt(code, 10).toChar.validNec
    else if (code.toLowerCase.matches("^-?0x[0-9a-f]+$"))
      Integer.parseInt(code.replace("0x", ""), 16).toChar.validNec
    else if (code.matches("^-?0\\d+$"))
      Integer.parseInt(code, 8).toChar.validNec
    else
      FlfCharacterError(s"Couldn't convert character code '$code' defined at line ${index + 1}").invalidNec

}
