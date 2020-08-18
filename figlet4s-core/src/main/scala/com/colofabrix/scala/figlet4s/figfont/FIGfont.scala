package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.utils._

/**
 * A FIGlet Font is a map of characters to their FIGrepresentation and the typographic settings used to display them
 */
final case class FIGfont private[figlet4s] (
    id: String,
    name: String,
    header: FIGheader,
    comment: String,
    settings: FIGfontSettings,
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
  lazy val zero: FIGcharacter =
    FIGcharacter(id, 0.toChar, SubLines.zero(header.height), '@', 0, None, -1)

  /**
   * Processes a character to a representable format
   */
  def process(char: Char): Vector[String] =
    this(char)
      .lines
      .replace(header.hardblank.toString, " ")
      .value

  override def toString: String =
    s"FIGfont(id=$id," +
    s"name=$name, " +
    s"header=$header, " +
    s"comment=$comment, " +
    s"settings=$settings)"
}

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial", "org.wartremover.warts.TraversableOps"))
object FIGfont {
  /** State to build a font that is filled while scanning input lines */
  final private case class FontBuilderState(
      name: String,
      header: Option[FIGheader] = None,
      commentLines: Vector[String] = Vector.empty,
      loadedNames: Set[Char] = Set.empty,
      loadedChars: Vector[CharBuilderState] = Vector.empty,
      loadedCharLines: Vector[String] = Vector.empty,
      processTaggedFonts: Boolean = false,
      hash: String = "".md5,
  )

  /** State to build a character that is filled while scanning input lines */
  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  final private case class CharBuilderState(
      name: Char,
      lines: Vector[String],
      comment: Option[String],
      position: Int,
  )

  /**
   * Creates a validated FIGfont with the given parameters
   */
  def apply(name: String, header: FIGheader, comment: String, chars: Vector[FIGcharacter]): FigletResult[FIGfont] = {
    val hash = (name + header.toString + comment + chars.mkString).md5

    val hLayoutV = HorizontalLayout.fromHeader(header)
    val vLayoutV = VerticalLayout.fromHeader(header)
    val charsV =
      chars
        .validNec
        .andThen(validatedRequiredChars)
        .andThen(validatedCharsUniformity)
        .map { validChars =>
          validChars
            .map(_.copy(fontId = hash))
            .map(char => char.name -> char)
            .toMap
        }

    val settingsV = (hLayoutV, vLayoutV).mapN {
      FIGfontSettings(_, _, PrintDirection.fromHeader(header))
    }

    (settingsV, charsV).mapN {
      FIGfont(hash, name, header, comment, _, _)
    }
  }

  /**
   * Creates a new FIGfont by parsing an input vector of lines representing an FLF file
   */
  def apply(name: String, lines: Iterable[String]): FigletResult[FIGfont] =
    lines
      .zipWithIndex
      .foldLeft(FontBuilderState(name).validNec[FigletError]) {
        case (i @ Invalid(_), _)           => i
        case (Valid(state), (line, index)) => processLine(state, line, index)
      }
      .andThen(buildFont)

  /** List of required characters that all FIGfont must define */
  private val requiredChars = ((32 to 126) ++ Seq(196, 214, 220, 228, 246, 252, 223)).map(_.toChar)

  /**
   * Processes a line calling the appropriate action based on the current state
   */
  private def processLine(state: FontBuilderState, line: String, index: Int): FigletResult[FontBuilderState] =
    if (index === 0)
      buildHeader(state, line)
    else if (index <= state.header.get.commentLines)
      buildComment(state, line)
    else if (!state.processTaggedFonts)
      buildCharacter(state, line, index)
    else
      buildTaggedCharacter(state, line, index)

  /**
   * Build the FIGfont by parsing the font builder state
   */
  private def buildFont(fontState: FontBuilderState): FigletResult[FIGfont] =
    if (fontState.loadedCharLines.size =!= 0) {
      // Check we didn't stop in the middle of a character
      FIGcharacterError("Incomplete character definition at the end of the file").invalidNec

    } else {
      val header   = fontState.header.get
      val nameV    = fontState.name.validNec
      val hashV    = fontState.hash.validNec
      val commentV = fontState.commentLines.mkString("\n").validNec
      val hLayoutV = HorizontalLayout.fromHeader(header)
      val vLayoutV = VerticalLayout.fromHeader(header)
      val settingsV = (hLayoutV, vLayoutV).mapN {
        FIGfontSettings(_, _, PrintDirection.fromHeader(header))
      }

      val charsV = fontState
        .loadedChars
        .traverse(buildChar(fontState, _))
        .andThen(validatedRequiredChars)
        .andThen { chars =>
          val loadedTaggedCount = chars.size - requiredChars.size
          val codetagCount      = header.codetagCount.getOrElse(loadedTaggedCount)

          if (loadedTaggedCount === codetagCount)
            chars.validNec
          else
            FIGFontError(
              s"The number of loaded tagged fonts $loadedTaggedCount doesn't correspond to the value " +
              s"indicated in the header $codetagCount",
            ).invalidNec
        }
        .map(_.map(c => c.name -> c).toMap)

      (hashV, nameV, header.validNec, commentV, settingsV, charsV)
        .mapN(FIGfont.apply)
    }

  /**
   * Check all required characters are present
   */
  private def validatedRequiredChars(chars: Vector[FIGcharacter]): FigletResult[Vector[FIGcharacter]] = {
    val loadedCharset = chars.map(_.name).toSet
    val missing       = requiredChars.toSet diff loadedCharset mkString ", "

    if (missing.nonEmpty)
      FIGcharacterError(s"Missing definition for required FIGlet characters: $missing").invalidNec
    else
      chars.validNec
  }

  /**
   * Check that the list of FIGcharacter are all uniform
   */
  private def validatedCharsUniformity(chars: Vector[FIGcharacter]): FigletResult[Vector[FIGcharacter]] =
    if (chars.map(_.lines.length).length =!= 1)
      FIGcharacterError(s"All FIGcharacters must have the same number of lines").invalidNec
    else if (chars.map(_.fontId).length =!= 1)
      FIGcharacterError(s"All FIGcharacters must have the same fontId, even if empty").invalidNec
    else
      chars.validNec

  /**
   * Build the FIGfont by parsing the character builder state
   */
  private def buildChar(fontState: FontBuilderState, charState: CharBuilderState): FigletResult[FIGcharacter] =
    fontState
      .header.map(
        FIGcharacter(fontState.hash, _, charState.name, SubLines(charState.lines), charState.comment, charState.position),
      )
      .get

  /**
   * Parses the FLF header
   */
  private def buildHeader(state: FontBuilderState, line: String): FigletResult[FontBuilderState] =
    FIGheader(line) andThen { header =>
      state.copy(header = Some(header)).validNec
    }

  /**
   * Builds the comment section
   */
  private def buildComment(state: FontBuilderState, line: String): FigletResult[FontBuilderState] =
    state.copy(commentLines = state.commentLines :+ line).validNec

  /**
   * Builds characters using the given state and the current line
   */
  private def buildCharacter(state: FontBuilderState, line: String, index: Int): FigletResult[FontBuilderState] = {
    val header          = state.header.get
    val loadedCharLines = state.loadedCharLines :+ line
    val hash            = (state.hash + line.md5).md5

    if (state.loadedCharLines.length + 1 < header.height) {
      state.copy(loadedCharLines = loadedCharLines, hash = hash).validNec

    } else {
      val startLine   = index - state.loadedCharLines.size
      val charNum     = (startLine - header.commentLines - 1) / header.height
      val charBuilder = CharBuilderState(requiredChars(charNum), state.loadedCharLines :+ line, None, startLine)

      state
        .copy(
          hash = hash,
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
  private def buildTaggedCharacter(
      state: FontBuilderState,
      line: String,
      index: Int,
  ): FigletResult[FontBuilderState] = {
    val header = state.header.get
    val hash   = (state.hash + line.md5).md5

    if (state.loadedCharLines.length + 1 < header.height + 1) {
      val loadedCharLines = state.loadedCharLines :+ line
      state.copy(loadedCharLines = loadedCharLines, hash = hash).validNec

    } else {
      val tagLineIndex    = index - state.loadedCharLines.size
      val nameV           = parseTagName(state.loadedCharLines.head, tagLineIndex)
      val commentV        = parseTagComment(state.loadedCharLines.head)
      val loadedCharLines = state.loadedCharLines.drop(1) :+ line

      (nameV, commentV)
        .mapN(CharBuilderState(_, loadedCharLines, _, tagLineIndex))
        .map { charBuilder =>
          state.copy(
            hash = hash,
            loadedCharLines = Vector.empty,
            loadedNames = state.loadedNames + charBuilder.name,
            loadedChars = state.loadedChars :+ charBuilder,
          )
        }
    }
  }

  /**
   * Parses the line of a tag to extract the name
   */
  private def parseTagName(tagLine: String, tagLineIndex: Int): FigletResult[Char] = {
    val splitFontTag = tagLine.replaceFirst(" +", "###").split("###").toVector
    Option
      .when(splitFontTag.nonEmpty)(splitFontTag(0))
      .toValidNec(
        FIGcharacterError(s"Missing character code in the tag at line ${tagLineIndex + 1}: $tagLine"),
      )
      .andThen(parseCharCode(tagLineIndex, _))
  }

  /**
   * Parses the line of a tag to extract the comment
   */
  private def parseTagComment(tagLine: String): FigletResult[Option[String]] = {
    val splitFontTag = tagLine.replaceFirst(" +", "###").split("###").toVector
    Option
      .when(splitFontTag.size > 1)(splitFontTag(1))
      .getOrElse("")
      .validNec[FigletError]
      .map(Option(_))
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
