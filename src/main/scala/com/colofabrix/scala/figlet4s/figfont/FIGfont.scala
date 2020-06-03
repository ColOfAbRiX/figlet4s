package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * A FIGlet Font is a map of characters to their FIGrepresentation and the typographic settings used to display them
 */
final case class FIGfont(
    name: String,
    header: FIGheader,
    comment: String,
    hLayout: HorizontalLayout,
    vLayout: VerticalLayout,
    characters: Map[Char, FIGcharacter],
) {
  /**
   * Returns a FIGcharacter representation of the given Char
   */
  def apply(char: Char): FIGcharacter =
    characters.getOrElse(char, characters('0'))

  /**
   * The empty character
   */
  val zero: FIGcharacter =
    FIGcharacter(this, 0.toChar, Vector.fill(header.height)(""), '@', 0, None, -1)

  /**
   * Processes a character to a representable format
   */
  def process(char: Char): Vector[String] =
    this(char)
      .lines
      .map(_.replace(header.hardblank, " "))
}

final object FIGfont {
  private case class BuilderState(
      name: String,
      header: Option[FIGheader] = None,
      commentLines: Vector[String] = Vector.empty,
      loadedNames: Set[Char] = Set.empty,
      loadedChars: Vector[CharBuilderState] = Vector.empty,
      loadedCharLines: Vector[String] = Vector.empty,
      processTaggedFonts: Boolean = false,
  )

  private case class CharBuilderState(
      name: Char,
      lines: Vector[String],
      comment: Option[String],
      position: Int,
  )

  /**
   * Creates a new FIGfont by parsing an input vector of lines representing an FLF file
   */
  def apply(name: String, lines: Iterable[String]): FigletResult[FIGfont] =
    lines
      .zipWithIndex
      .foldLeft(BuilderState(name).validNec[FigletError]) {
        case (i @ Invalid(_), _)           => i
        case (Valid(state), (line, index)) => processLine(state, line, index)
      }
      .andThen(parseFinalBuilderState _)

  /**
   * Creates a new FIGfont with FIGcharacters given as Vector
   */
  private[figlet4s] def apply(
      name: String,
      header: FIGheader,
      comment: String,
      characters: Vector[FIGcharacter],
  ): FIGfont =
    FIGfont(name, header, comment, characters.map(c => c.name -> c).toMap)

  private val requiredChars = ((32 to 126) ++ Seq(196, 214, 220, 228, 246, 252, 223)).map(_.toChar)

  /**
   * Build the FIGfont by parsing the given state
   */
  private def parseFinalBuilderState(state: BuilderState): FigletResult[FIGfont] =
    if (state.loadedCharLines.size != 0) {
      // Check we didn't stop in the middle of a character
      FIGcharacterError("Incomplete character definition").invalidNec

    } else if ((requiredChars.toSet diff state.loadedNames).size != 0) {
      // Check we loaded all required characters
      val missing = requiredChars.toSet diff state.loadedNames mkString (", ")
      FIGcharacterError(s"Missing definition for required FIGlet characters: $missing").invalidNec

    } else {
      val header    = state.header.get
      val comment   = state.commentLines.mkString("\n")
      val emptyFont = FIGfont(state.name, header, comment, Vector.empty)

      // // Build the font
      // val a = state.loadedChars.foldLeft(emptyFont) { (font, builder) =>
      //   font.addCharacter(builder.name, builder.lines, builder.comment, builder.position)
      // }

      ???
    }

  /**
   * Build the FIGchar by parsing the CharBuilderState
   */
  private def parseCharBuilder(
      font: () => FIGfont,
      state: BuilderState,
      charState: CharBuilderState,
  ): FigletResult[FIGcharacter] = {
    ???
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
    FIGheader(line) andThen { header =>
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
      val startLine   = index - state.loadedCharLines.size
      val charNum     = (startLine - header.commentLines - 1) / header.height
      val charBuilder = CharBuilderState(requiredChars(charNum), state.loadedCharLines :+ line, None, startLine)
      state
        .copy(
          loadedCharLines = Vector.empty,
          loadedNames = state.loadedNames + charBuilder.name,
          loadedChars = state.loadedChars :+ charBuilder,
          processTaggedFonts = state.loadedChars.size + 1 >= requiredChars.size,
        ).validNec
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
        .toValidNec(FIGcharacterError(s"Missing character code at line ${index + 1}: $line"))
        .andThen(parseCharCode(startLine, _))

      val commentV = Option
        .when(splitFontTag.size > 1)(splitFontTag(1))
        .getOrElse("")
        .validNec[FigletError]
        .map(Option(_))

      val loadedCharLines = state.loadedCharLines.drop(1) :+ line

      (nameV, commentV)
        .mapN(CharBuilderState(_, loadedCharLines, _, startLine))
        .map { charBuilder =>
          state.copy(
            loadedCharLines = Vector.empty,
            loadedNames = state.loadedNames + charBuilder.name,
            loadedChars = state.loadedChars :+ charBuilder,
          )
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
      FIGcharacterError(s"Couldn't convert character code '$code' defined at line ${index + 1}").invalidNec

}
