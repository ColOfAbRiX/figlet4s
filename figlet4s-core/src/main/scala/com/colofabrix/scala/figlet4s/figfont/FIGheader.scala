package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._

/**
 * FIGLettering Font file header that contains raw configuration settings for the FIGfont
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
  override def toString: String =
    s"FIGheader(signature=$signature, " +
    s"hardblank=$hardblank, " +
    s"height=$height, " +
    s"baseline=$baseline, " +
    s"maxLength=$maxLength, " +
    s"oldLayout=$oldLayout, " +
    s"commentLines=$commentLines, " +
    s"printDirection=$printDirection, " +
    s"fullLayout=$fullLayout, " +
    s"codetagCount=$codetagCount)"

  def singleLine(): String = {
    val oldLayoutNum      = oldLayout.map(_.value).sum.toString
    val printDirectionNum = printDirection.map(x => s" ${x.value}").getOrElse("")
    val fullLayoutNum     = fullLayout.map(x => s" ${x.map(_.value).sum}").getOrElse("")
    val codetagCountNum   = codetagCount.map(x => s" $x").getOrElse("")

    s"$signature$hardblank $height $baseline $maxLength $oldLayoutNum $commentLines $printDirectionNum $fullLayoutNum $codetagCountNum"
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

  /**
   * Creates a new FLF Header from a the string representing the header
   */
  def apply(line: String): FigletResult[FIGheader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.length < 6)
      FIGheaderError(
        s"Wrong number of parameters in FLF header. Found ${splitLine.length} parameters",
      ).invalidNec
    else {
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

      // format: off
      (signatureV, hardblankV, heightV, baselineV, maxLengthV, oldLayoutV, commentLinesV, printDirectionV, fullLayoutV, codetagCountV)
        .mapN(FIGheader.apply)
      // format: on
    }
  }

  private def validateSignature(signature: String): FigletResult[String] =
    Option
      .when(signature === "flf2a")(signature)
      .toValidNec(FIGheaderError(s"Wrong FLF signature: $signature"))

  private def validateHardblank(hardblank: String): FigletResult[Char] =
    Option
      .when(hardblank.length === 1)(hardblank)
      .toValidNec(FIGheaderError(s"The hardblank '$hardblank' is not composed of only one character"))
      .map(_.charAt(0))

  private def validateHeight(height: String): FigletResult[Int] =
    height
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'height': $height"))
      .andThen { value =>
        Validated.condNec(value > 0, value, FIGheaderError(s"Field 'height' must be positive: $height"))
      }

  private def validateBaseline(baseline: String): FigletResult[Int] =
    baseline
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'baseline': $baseline"))
      .andThen { value =>
        Validated.condNec(value > 0, value, FIGheaderError(s"Field 'baseline' must be positive: $baseline"))
      }

  private def validateMaxLength(maxLength: String): FigletResult[Int] =
    maxLength
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'maxLength': $maxLength"))
      .andThen { value =>
        Validated.condNec(value > 0, value, FIGheaderError(s"Field 'maxLength' must be positive: $maxLength"))
      }

  private def validateOldLayout(oldLayout: String): FigletResult[Vector[OldLayout]] =
    oldLayout
      .toIntOption
      .map(OldLayout(_))
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'oldLayout': $oldLayout"))

  private def validateCommentLines(commentLines: String): FigletResult[Int] =
    commentLines
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'commentLines': $commentLines"))
      .andThen { value =>
        Validated.condNec(value > 0, value, FIGheaderError(s"Field 'commentLines' must be positive: $commentLines"))
      }

  private def validatePrintDirection(printDirection: Option[String]): FigletResult[Option[PrintDirection]] =
    printDirection.traverse { value =>
      value
        .toIntOption
        .map(PrintDirection(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'printDirection': $printDirection"))
    }

  private def validateFullLayout(fullLayout: Option[String]): FigletResult[Option[Vector[FullLayout]]] =
    fullLayout.traverse { value =>
      value
        .toIntOption
        .map(FullLayout(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'fullLayout': $fullLayout"))
    }

  private def validateCodetagCount(codetagCount: Option[String]): FigletResult[Option[Int]] =
    codetagCount.traverse { value =>
      value
        .toIntOption
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'codetagCount': $codetagCount"))
    }
}
