package com.colofabrix.scala.figlet4s.figfont.header

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._

/**
 * FIGLet File Header
 */
case class FlfHeader(
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
  def fromString(line: String): FigletResult[FlfHeader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.length < 7 || splitLine.length > 10) {
      FLFError("Wrong number of argument in FLF header").invalidNec

    } else {
      val (signature, hardblank) = splitLine(SIGNATURE_INDEX).splitAt(5)

      val signatureV =
        if (signature != "flf2a") FLFError("Wrong FLF signature").invalidNec
        else "flf2a".validNec

      val hardblankV =
        if (hardblank.length != 1) FLFError("Wrong hardblank character after signature").invalidNec
        else hardblank.validNec

      val heightV = splitLine(HEIGHT_INDEX)
        .toIntOption
        .map(_.validNec)
        .getOrElse(FLFError("Couldn't parse field 'height' to Int").invalidNec)

      val baselineV = splitLine(BASELINE_INDEX)
        .toIntOption
        .map(_.validNec)
        .getOrElse(FLFError("Couldn't parse field 'baseline' to Int").invalidNec)

      val maxLengthV = splitLine(MAXLENGTH_INDEX)
        .toIntOption
        .map(_.validNec)
        .getOrElse(FLFError("Couldn't parse field 'maxLength' to Int").invalidNec)

      val oldLayoutV = splitLine(OLDLAYOUT_INDEX)
        .toIntOption
        .map(OldLayout(_).validNec)
        .getOrElse(FLFError("Couldn't parse field 'oldLayout' to OldLayout").invalidNec)

      val commentLinesV = splitLine(COMMENTLINES_INDEX)
        .toIntOption
        .map(_.validNec)
        .getOrElse(FLFError("Couldn't parse field 'commentLines' to Int").invalidNec)

      val printDirectionV = Option
        .when(splitLine.size > PRINTDIRECTION_INDEX) {
          splitLine(PRINTDIRECTION_INDEX)
            .toIntOption
            .map(PrintDirection(_).validNec)
            .getOrElse(FLFError("Couldn't parse field 'printDirection' to PrintDirection").invalidNec)
        }.sequence

      val fullLayoutV = Option
        .when(splitLine.size > FULLLAYOUT_INDEX) {
          splitLine(FULLLAYOUT_INDEX)
            .toIntOption
            .map(FullLayout(_).validNec)
            .getOrElse(FLFError("Couldn't parse field 'fullLayout' to FullLayout").invalidNec)
        }.sequence

      val codetagCountV = Option
        .when(splitLine.size > CODETAGCOUNT_INDEX) {
          splitLine(CODETAGCOUNT_INDEX)
            .toIntOption
            .map(_.validNec)
            .getOrElse(FLFError("Couldn't parse field 'codetagCount' to Int").invalidNec)
        }.sequence

      (
        signatureV,
        hardblankV,
        heightV,
        baselineV,
        maxLengthV,
        oldLayoutV,
        commentLinesV,
        printDirectionV,
        fullLayoutV,
        codetagCountV,
      ).mapN(FlfHeader.apply)
    }
  }
}
