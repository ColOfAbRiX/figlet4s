package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._

/**
 * FIGLettering Font file header that contains raw configuration settings for the FIGfont
 *
 * A FIGheader cannot be instantiated directly as a case class but one must go through the factory methods defined
 * in the companion object [[FIGheader$ FIGheader]] that perform validation of the defining lines of the header
 *
 * @param signature      The first five characters of the header, always "flf2a"
 * @param hardblank      Which sub-character is used to represent hardblanks in the FIGcharacter data, usually '$'
 * @param height         The height of every FIGcharacter
 * @param baseline       The number of lines from the baseline of a FIGcharacter to the top of the tallest FIGcharacter
 * @param maxLength      The maximum length of any line describing a FIGcharacter
 * @param oldLayout      Describes information about horizontal layout but does not include all of the information
 *                       desired by the most recent FIGdrivers
 * @param commentLines   How many lines of comments there are before the character definitions begin
 * @param printDirection Which direction the font is to be printed by default
 * @param fullLayout     Describes ALL information about horizontal and vertical layout
 * @param codetagCount   The number of code-tagged (non-required) FIGcharacter
 */
final case class FIGheader private[figlet4s] (
    signature: String,
    hardblank: Char,
    height: Int,
    baseline: Int,
    maxLength: Int,
    oldLayout: Seq[OldLayout],
    commentLines: Int,
    printDirection: Option[PrintDirection],
    fullLayout: Option[Seq[FullLayout]],
    codetagCount: Option[Int],
) {

  /**
   * Returns the single-line representation of the FIGheader as defined in the FLF standard
   *
   * @return A String containing the FLF representation of the FIGheader
   */
  def singleLine(): String = {
    val oldLayoutNum      = oldLayout.map(_.value).sum.toString
    val printDirectionNum = printDirection.map(x => s" ${x.value}").getOrElse("")
    val fullLayoutNum     = fullLayout.map(x => s" ${x.map(_.value).sum}").getOrElse("")
    val codetagCountNum   = codetagCount.map(x => s" $x").getOrElse("")

    s"$signature$hardblank $height $baseline $maxLength $oldLayoutNum $commentLines$printDirectionNum$fullLayoutNum$codetagCountNum"
  }

}

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

  /** Identification part of the header */
  final private case class HeaderIdentity(signature: String, hardblank: Char)

  /** Size part of the header */
  final private case class HeaderDimensions(height: Int, baseline: Int, maxLength: Int)

  /** Layout part of the header */
  final private case class HeaderLayout(
      oldLayout: Seq[OldLayout],
      commentLines: Int,
      printDirection: Option[PrintDirection],
  )

  /** Tagged-characters part of the header */
  final private case class HeaderTags(fullLayout: Option[Seq[FullLayout]], codetagCount: Option[Int])

  /**
   * Creates a new FLF Header from a the string representing the header
   *
   * @param line The String containing the full header of the FLF file
   * @return A [[com.colofabrix.scala.figlet4s.FigletResult FigletResult]] containing the new FIGheader or a list
   *         of errors occurred during the creation
   */
  // format: off
  def apply(line: String): FigletResult[FIGheader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.lengthIs < 6)
      FIGheaderError(s"Wrong number of parameters in FLF header. Found ${splitLine.length} parameters").invalid
    else {
      val requiredFields = FigletResult.sequence(
        Seq(
          splitLine.lift(SIGNATURE_INDEX).toValid(FIGheaderError("Missing signature field in header")),
          splitLine.lift(HEIGHT_INDEX).toValid(FIGheaderError("Missing height field in header")),
          splitLine.lift(BASELINE_INDEX).toValid(FIGheaderError("Missing baseline field in header")),
          splitLine.lift(MAXLENGTH_INDEX).toValid(FIGheaderError("Missing maxLength field in header")),
          splitLine.lift(OLDLAYOUT_INDEX).toValid(FIGheaderError("Missing oldLayout field in header")),
          splitLine.lift(COMMENTLINES_INDEX).toValid(FIGheaderError("Missing commentLines field in header")),
        ),
      )

      requiredFields.andThen {
        case Seq(signatureField, heightField, baselineField, maxLengthField, oldLayoutField, commentLinesField) =>
          val (signatureText, hardblankText) = signatureField.splitAt(5)

          val identityV =
            (validateSignature(signatureText), validateHardblank(hardblankText))
            .mapN(HeaderIdentity.apply)

          val dimensionsV =
            (validateHeight(heightField), validateBaseline(baselineField), validateMaxLength(maxLengthField))
              .mapN(HeaderDimensions.apply)

          val layoutV =
            (
              validateOldLayout(oldLayoutField),
              validateCommentLines(commentLinesField),
              validatePrintDirection(splitLine.lift(PRINTDIRECTION_INDEX)),
            ).mapN(HeaderLayout.apply)
            
          val tagsV =
            (
              validateFullLayout(splitLine.lift(FULLLAYOUT_INDEX)),
              validateCodetagCount(splitLine.lift(CODETAGCOUNT_INDEX)),
            ).mapN(HeaderTags.apply)

          (identityV, dimensionsV, layoutV, tagsV).mapN { (identity, dimensions, layout, tags) =>
            FIGheader(
              identity.signature,
              identity.hardblank,
              dimensions.height,
              dimensions.baseline,
              dimensions.maxLength,
              layout.oldLayout,
              layout.commentLines,
              layout.printDirection,
              tags.fullLayout,
              tags.codetagCount,
            )
          }

        case _ =>
          // Unreachable: the sequence is built with exactly six fields
          FIGheaderError("Invalid number of fields in FLF header").invalid
      }
    }
  }
  // format: on

  private def validateSignature(signature: String): FigletResult[String] =
    Option
      .when(signature === "flf2a")(signature)
      .toValid(FIGheaderError(s"Wrong FLF signature: $signature"))

  private def validateHardblank(hardblank: String): FigletResult[Char] =
    Option
      .when(hardblank.length === 1)(hardblank)
      .toValid(FIGheaderError(s"The hardblank '$hardblank' is not composed of only one character"))
      .map(_.charAt(0))

  private def validateHeight(height: String): FigletResult[Int] =
    height
      .toIntOption
      .toValid(FIGheaderError(s"Couldn't parse header field 'height': $height"))
      .andThen { value =>
        FigletResult.ensure(value > 0, value, FIGheaderError(s"Field 'height' must be positive: $height"))
      }

  private def validateBaseline(baseline: String): FigletResult[Int] =
    baseline
      .toIntOption
      .toValid(FIGheaderError(s"Couldn't parse header field 'baseline': $baseline"))
      .andThen { value =>
        FigletResult.ensure(value >= 0, value, FIGheaderError(s"Field 'baseline' must be non-negative: $baseline"))
      }

  private def validateMaxLength(maxLength: String): FigletResult[Int] =
    maxLength
      .toIntOption
      .toValid(FIGheaderError(s"Couldn't parse header field 'maxLength': $maxLength"))
      .andThen { value =>
        FigletResult.ensure(value > 0, value, FIGheaderError(s"Field 'maxLength' must be positive: $maxLength"))
      }

  private def validateOldLayout(oldLayout: String): FigletResult[Vector[OldLayout]] =
    oldLayout
      .toIntOption
      .toValid(FIGheaderError(s"Couldn't parse header field 'oldLayout': $oldLayout"))
      .andThen { value =>
        val valid = value >= -1 && value <= 63
        FigletResult.ensure(
          valid,
          value,
          FIGheaderError(s"Field 'oldLayout' must be between -1 and 63, both included: $value"),
        )
      }
      .andThen(OldLayout(_))

  private def validateCommentLines(commentLines: String): FigletResult[Int] =
    commentLines
      .toIntOption
      .toValid(FIGheaderError(s"Couldn't parse header field 'commentLines': $commentLines"))
      .andThen { value =>
        FigletResult.ensure(
          value >= 0,
          value,
          FIGheaderError(s"Field 'commentLines' must be non-negative: $commentLines"),
        )
      }

  private def validatePrintDirection(printDirection: Option[String]): FigletResult[Option[PrintDirection]] =
    printDirection.traverse { value =>
      value
        .toIntOption
        .toValid(FIGheaderError(s"Couldn't parse header field 'printDirection': $printDirection"))
        .andThen {
          PrintDirection(_).mapErrors { error =>
            FIGheaderError(s"Invalid value for field 'printDirection': $value", error)
          }
        }
    }

  private def validateFullLayout(fullLayout: Option[String]): FigletResult[Option[Vector[FullLayout]]] =
    fullLayout.traverse { value =>
      value
        .toIntOption
        .toValid(FIGheaderError(s"Couldn't parse header field 'fullLayout': $fullLayout"))
        .andThen { value =>
          FigletResult.ensure(
            value >= 0 && value <= 32767,
            value,
            FIGheaderError(s"Field 'fullLayout' must be between 0 and 32767, both included: $value"),
          )
        }
        .andThen(FullLayout(_))
    }

  private def validateCodetagCount(codetagCount: Option[String]): FigletResult[Option[Int]] =
    codetagCount.traverse { value =>
      value
        .toIntOption
        .toValid(FIGheaderError(s"Couldn't parse header field 'codetagCount': $codetagCount"))
        .andThen { value =>
          FigletResult.ensure(
            value >= 0,
            value,
            FIGheaderError(s"Field 'codetagCount' must be non-negative: $codetagCount"),
          )
        }
    }

}
