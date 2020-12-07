package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.errors._
import scala.collection.immutable.ListMap

final case class TestHeader(
    signature: String = StandardFont.signature,
    hardblank: String = StandardFont.hardblank,
    height: String = StandardFont.height,
    baseline: String = StandardFont.baseline,
    maxLength: String = StandardFont.maxLength,
    oldLayout: String = StandardFont.oldLayout,
    commentLines: String = StandardFont.commentLines,
    printDirection: String = StandardFont.printDirection,
    fullLayout: String = StandardFont.fullLayout,
    codetagCount: String = StandardFont.codetagCount,
) {
  // format: off
  val toList: List[String] = List(
    signature, hardblank, height, baseline, maxLength, oldLayout,
    commentLines, printDirection, fullLayout, codetagCount
  )
  // format: on

  def toFIGheader: FigletResult[FIGheader] =
    FIGheader(this.toLine)

  def toLine: String =
    toList.mkString(" ").replaceFirst(" ", "")

  def noPrintDirection: String =
    toList.dropRight(3).mkString(" ").replaceFirst(" ", "")

  def noFullLayout: String =
    toList.dropRight(2).mkString(" ").replaceFirst(" ", "")

  def noCodetagCount: String =
    toList.dropRight(1).mkString(" ").replaceFirst(" ", "")
}

object TestCharacter {
  def flatMap(f: (String, Int) => Vector[String]): SubLines =
    getFlatMap("036")(f)

  def getFlatMap(name: String)(f: (String, Int) => Vector[String]): SubLines =
    SubLines(StandardFont.characters(name).split("\n").zipWithIndex.toIndexedSeq.flatMap(f.tupled))

  def get(
      charName: Char,
      lines: SubLines = getFlatMap("032")((x: String, _: Int) => Vector(x)),
      maxWidth: Int = TestHeader().maxLength.toInt,
      height: Int = TestHeader().height.toInt,
  ): FigletResult[FIGcharacter] =
    TestHeader()
      .copy(maxLength = maxWidth.toString, height = height.toString)
      .toFIGheader
      .andThen { h =>
        FIGcharacter("", h, charName, lines, None, 123)
      }
}

case class TestFont(
    header: TestHeader = TestHeader(),
    comment: String = StandardFont.comment,
    characters: ListMap[String, String] = StandardFont.characters,
    codeTag: ListMap[String, String] = StandardFont.codeTag,
) {
  def allLines(includeTags: Boolean = true): Vector[String] = {
    val updatedHeader =
      if (!includeTags) header.copy(codetagCount = "0")
      else header

    val partial =
      updatedHeader.toLine +:
      comment.split("\n") ++:
      characters.values.map(_.split("\n")).flatten.toVector

    if (includeTags) partial ++: codeTag.values.map(_.split("\n")).flatten.toVector else partial
  }

  def flatMapChars(includeTags: Boolean = true)(f: (String, Int) => Vector[String]): Vector[String] = {
    val updatedHeader =
      if (!includeTags) header.copy(codetagCount = "0")
      else header

    val newChars =
      characters
        .values
        .zipWithIndex
        .flatMap(f.tupled)
        .map(_.split("\n"))
        .flatten
        .toVector

    val partial =
      updatedHeader.toLine +:
      comment.split("\n") ++:
      newChars

    if (includeTags) partial ++: codeTag.values.map(_.split("\n")).flatten.toVector else partial
  }

  def flatMapTagged(f: (String, Int) => Vector[String]): Vector[String] = {
    val newTagged =
      codeTag
        .values
        .zipWithIndex
        .flatMap(f.tupled)
        .map(_.split("\n"))
        .flatten
        .toVector

    header.toLine +:
    comment.split("\n") ++:
    characters.values.map(_.split("\n")).flatten.toVector ++:
    newTagged
  }
}
