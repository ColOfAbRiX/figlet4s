package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._
import _root_.cats.data.Validated

/**
 * FIGlet File Header
 */
final case class FIGheader(
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

object FIGheader {
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
  def apply(line: String): FigletResult[FIGheader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.length < 6 || splitLine.length > 9) {
      FIGheaderError(s"Wrong number of parameters in FLF header. Found ${splitLine.length.toString} parameters").invalidNec

    } else {
      val (signatureText, hardblankText) = splitLine(SIGNATURE_INDEX).splitAt(5)

      val signatureV      = validateSignature(signatureText)
      val hardblankV      = validateHardblank(hardblankText)
      val heightV         = validateHeight(splitLine(HEIGHT_INDEX))
      val baselineV       = validateBaseline(splitLine(BASELINE_INDEX))
      val maxLengthV      = validateMaxLength(splitLine(MAXLENGTH_INDEX))
      val oldLayoutV      = validateOldLayout(splitLine(OLDLAYOUT_INDEX))
      val commentLinesV   = validateCommentLines(splitLine(COMMENTLINES_INDEX))
      val printDirectionV = validatePrintDirection(splitLine.get(PRINTDIRECTION_INDEX.toLong))
      val fullLayoutV     = validateFullLayout(splitLine.get(FULLLAYOUT_INDEX.toLong))
      val codetagCountV   = validateCodetagCount(splitLine.get(CODETAGCOUNT_INDEX.toLong))

      val crossValidatedLayoutsV = (oldLayoutV, fullLayoutV)
        .mapN { (oldLayout, fullLayoutO) =>
          fullLayoutO.map(crossValidateLayouts(oldLayout, _))
        }

      // format: off
      crossValidatedLayoutsV andThen { _ =>
        (signatureV, hardblankV, heightV, baselineV, maxLengthV, oldLayoutV, commentLinesV, printDirectionV, fullLayoutV, codetagCountV)
          .mapN(FIGheader.apply)
      }
      // format: on
    }
  }

  /**
   * Identify the file as compatible with FIGlet version 2.0 or later
   */
  private def validateSignature(signature: String): FigletResult[String] =
    Option
      .when(signature == "flf2a")(signature)
      .toValidNec(FIGheaderError(s"Wrong FLF signature: $signature"))

  /**
   * The header line defines which sub-character will be used to represent hardblanks in the FIGcharacter data
   */
  private def validateHardblank(hardblank: String): FigletResult[String] =
    Option
      .when(hardblank.length == 1)(hardblank)
      .toValidNec(FIGheaderError(s"The hardblank '$hardblank' is not composed of only one character"))

  /**
   * The consistent height of every FIGcharacter, measured in subcharacters
   */
  private def validateHeight(height: String): FigletResult[Int] =
    height
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'height': $height"))

  /**
   * The number of lines of subcharacters from the baseline of a FIGcharacter to the top of the tallest FIGcharacter
   */
  private def validateBaseline(baseline: String): FigletResult[Int] =
    baseline
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'baseline': $baseline"))

  /**
   * The maximum length of any line describing a FIGcharacter
   */
  private def validateMaxLength(maxlength: String): FigletResult[Int] =
    maxlength
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'maxLength': $maxlength"))

  /**
   * Describes information about horizontal and vertical layout but does not include all of the information desired
   * by the most recent FIGdrivers
   */
  private def validateOldLayout(oldLayout: String): FigletResult[Vector[OldLayout]] =
    oldLayout
      .toIntOption
      .map(OldLayout(_))
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'oldLayout': $oldLayout"))

  /**
   * How many lines of comments there are
   */
  private def validateCommentLines(commentLines: String): FigletResult[Int] =
    commentLines
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'commentLines': $commentLines"))

  /**
   * Which direction the font is to be printed by default
   */
  private def validatePrintDirection(printDirection: Option[String]): FigletResult[Option[PrintDirection]] =
    printDirection.traverse { value =>
      value
        .toIntOption
        .map(PrintDirection(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'printDirection': $printDirection"))
    }

  /**
   * Describes ALL information about horizontal and vertical layout
   */
  private def validateFullLayout(fullLayout: Option[String]): FigletResult[Option[Vector[FullLayout]]] =
    fullLayout.traverse { value =>
      value
        .toIntOption
        .map(FullLayout(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'fullLayout': $fullLayout"))
    }

  /**
   * The number of code-tagged (non-required) FIGcharacters in this FIGfont
   */
  private def validateCodetagCount(codetagCount: Option[String]): FigletResult[Option[Int]] =
    codetagCount.traverse { value =>
      value
        .toIntOption
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'codetagCount': $codetagCount"))
    }

  /**
   * Validates the compatibility of the old and full layout options
   */
  private def crossValidateLayouts(
      oldLayout: Vector[OldLayout],
      fullLayout: Vector[FullLayout],
  ): FigletResult[Unit] = {
    val fullWidthV =
      if (FullLayout.isFullWidth(fullLayout) && OldLayout.isFullWidth(oldLayout)) ().validNec
      else FIGheaderError("FullLayout sets FullWidth but OldLayout doesn't set the same option").invalidNec

    val horizontalFittingV =
      if (FullLayout.isHorizontalFitting(fullLayout) && OldLayout.isFitting(oldLayout)) ().validNec
      else FIGheaderError("FullLayout sets Horizontal Fitting but OldLayout doesn't set the same option").invalidNec

    (fullWidthV, horizontalFittingV)
      .mapN((_, _) => ())
  }

}
