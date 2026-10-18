package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import org.scalacheck._

/** Generator of the texts used by the rendering tests */
object OriginalFigletTexts {

  val generator: Gen[String] =
    Gen
      .chooseNum(0, 20)
      .flatMap(length => Gen.listOfN(length, Gen.oneOf(safeCharset)))
      .map(_.mkString)

  // The generator produces texts from 0 (the empty-input edge case) up to 20 characters. I found issues when
  // rendering higher-number characters with figlet so I decided to work on only a subset of a font's charset: the
  // ideal scenario would be to test all possible characters: font.characters.keySet
  private def safeCharset: Seq[Char] =
    (32 to 126).map(_.toChar)

}
