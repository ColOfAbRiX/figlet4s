package com.colofabrix.scala.figlet4s.figfont.header

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._

/**
 * FIGlet File Header
 */
final case class FlfHeader(
    signature: String,
    hardblank: String,
    height: Int,
    baseline: Int,
    maxLength: Int,
    oldLayout: Vector[OldLayout],
    commentLines: Int,
    printDirection: Option[PrintDirection],
    fullLayout: Option[Vector[FullLayout]],
    codetagCount: Option[Int],
)

object FlfHeader {
  private val SIGNATURE_INDEX: Int      = 0
  private val HEIGHT_INDEX: Int         = 1
  private val BASELINE_INDEX: Int       = 2
  private val MAXLENGTH_INDEX: Int      = 3
  private val OLDLAYOUT_INDEX: Int      = 4
  private val COMMENTLINES_INDEX: Int   = 5
  private val PRINTDIRECTION_INDEX: Int = 6
  private val FULLLAYOUT_INDEX: Int     = 7
  private val CODETAGCOUNT_INDEX: Int   = 8

  /**
   * Creates a new FLF Header from a string
   */
  def apply(line: String): FigletResult[FlfHeader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.length < 6 || splitLine.length > 9) {
      FlfHeaderError(s"Wrong number of argument in FLF header: found ${splitLine.length.toString}").invalidNec

    } else {
      val (signatureText, hardblankText) = splitLine(SIGNATURE_INDEX).splitAt(5)

      // Identify the file as compatible with FIGlet version 2.0 or later
      val signatureV = Option
        .when(signatureText == "flf2a")(signatureText)
        .toValidNec(FlfHeaderError(s"Wrong FLF signature: $signatureText"))

      // The header line defines which sub-character will be used to represent hardblanks in the FIGcharacter data
      val hardblankV = Option
        .when(hardblankText.length == 1)(hardblankText)
        .toValidNec(FlfHeaderError(s"The hardblank is not composed of only one character: $hardblankText"))

      // The consistent height of every FIGcharacter, measured in subcharacters
      val heightV = splitLine(HEIGHT_INDEX)
        .toIntOption
        .toValidNec(FlfHeaderError(s"Couldn't parse header field 'height' to Int"))

      // The number of lines of subcharacters from the baseline of a FIGcharacter to the top of the tallest FIGcharacter
      val baselineV = splitLine(BASELINE_INDEX)
        .toIntOption
        .toValidNec(FlfHeaderError(s"Couldn't parse header field 'baseline' to Int"))

      // The maximum length of any line describing a FIGcharacter
      val maxLengthV = splitLine(MAXLENGTH_INDEX)
        .toIntOption
        .toValidNec(FlfHeaderError(s"Couldn't parse header field 'maxLength' to Int"))

      // Describes information about horizontal and vertical layout but does not include all of the information desired
      // by the most recent FIGdrivers
      val oldLayoutV = splitLine(OLDLAYOUT_INDEX)
        .toIntOption
        .map(OldLayout(_))
        .toValidNec(FlfHeaderError(s"Couldn't parse header field 'oldLayout' to OldLayout"))

      // How many lines there are
      val commentLinesV = splitLine(COMMENTLINES_INDEX)
        .toIntOption
        .toValidNec(FlfHeaderError(s"Couldn't parse header field 'commentLines' to Int"))

      // Which direction the font is to be printed by default
      val printDirectionV = Option
        .when(splitLine.size > PRINTDIRECTION_INDEX) {
          splitLine(PRINTDIRECTION_INDEX)
            .toIntOption
            .map(PrintDirection(_))
            .toValidNec(FlfHeaderError(s"Couldn't parse header field 'printDirection' to PrintDirection"))
        }.sequence

      // Describes ALL information about horizontal and vertical layout
      val fullLayoutV = Option
        .when(splitLine.size > FULLLAYOUT_INDEX) {
          splitLine(FULLLAYOUT_INDEX)
            .toIntOption
            .map(FullLayout(_))
            .toValidNec(FlfHeaderError(s"Couldn't parse header field 'fullLayout' to FullLayout"))
        }.sequence

      // The number of code-tagged (non-required) FIGcharacters in this FIGfont
      val codetagCountV = Option
        .when(splitLine.size > CODETAGCOUNT_INDEX) {
          splitLine(CODETAGCOUNT_INDEX)
            .toIntOption
            .toValidNec(FlfHeaderError(s"Couldn't parse header field 'codetagCount' to Int"))
        }.sequence

      // format: off
      (signatureV, hardblankV, heightV, baselineV, maxLengthV, oldLayoutV, commentLinesV, printDirectionV, fullLayoutV, codetagCountV)
        .mapN(FlfHeader.apply)
      // format: on
    }
  }
}
