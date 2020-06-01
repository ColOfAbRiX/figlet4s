package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.header._

/**
 * FIGlet Font
 */
final case class FIGfont(header: FlfHeader, comment: String, characters: Map[Char, FIGcharacter])

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
      FlfCharacterError("Incomplete character definition found at the end of the file").invalidNec

    } else if ((requiredChars.toSet diff state.loadedChars.keySet).size != 0) {
      // Check we loaded all required characters
      val missing = requiredChars.toSet diff state.loadedChars.keySet
      FlfCharacterError(s"Missing definition for required characters: ${missing}").invalidNec

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
      storeComment(state, line)
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
  private def storeComment(state: BuilderState, line: String): FigletResult[BuilderState] =
    state.copy(commentLines = state.commentLines :+ line).validNec

  /**
   * Builds characters using the given state and the current line
   */
  private def buildCharacter(state: BuilderState, line: String, index: Int): FigletResult[BuilderState] = {
    val header = state.header.get

    if (state.loadedCharLines.length + 1 < header.height) {
      state.copy(loadedCharLines = state.loadedCharLines :+ line).validNec

    } else {
      val firstLine = 1 + header.commentLines
      val charIndex = (index - firstLine - header.height + 1) / header.height
      val valueV    = FIGcharacter(requiredChars(charIndex), state.loadedCharLines :+ line, header.height, "")

      valueV.map { charValue =>
        val loadedChars  = state.loadedChars + (charValue.name -> charValue)
        val isNextTagged = state.loadedChars.size + 1 >= requiredChars.size
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
      val splitTag = state.loadedCharLines.head.replaceFirst(" +", "###").split("###").toVector

      val nameV = Option
        .when(splitTag.size > 0)(splitTag(0))
        .toValidNec(FlfCharacterError(s"Missing character code at line $index: $line"))
        .andThen(parseCharCode _)

      val commentV = Option
        .when(splitTag.size > 1)(splitTag(1))
        .getOrElse("")
        .validNec[FigletError]

      val loadedCharLines = state.loadedCharLines.drop(1) :+ line

      val valueV = (nameV, commentV).mapN(FIGcharacter(_, loadedCharLines, header.height, _)) andThen identity

      valueV.map { charValue =>
        val loadedChars = state.loadedChars + (charValue.name -> charValue)
        state.copy(loadedCharLines = Vector.empty, loadedChars = loadedChars)
      }
    }
  }

  /**
   * Parses a character code into a Char
   */
  private def parseCharCode(code: String): FigletResult[Char] =
    if (code.matches("^-?\\d+$"))
      Integer.parseInt(code, 10).toChar.validNec
    else if (code.toLowerCase.matches("^-?0x[0-9a-f]+$"))
      Integer.parseInt(code.replace("0x", ""), 16).toChar.validNec
    else if (code.matches("^-?0\\d+$"))
      Integer.parseInt(code, 8).toChar.validNec
    else
      FlfCharacterError(s"Couldn't convert character code '$code' to Char").invalidNec

}
