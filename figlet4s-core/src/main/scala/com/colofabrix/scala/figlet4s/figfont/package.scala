package com.colofabrix.scala.figlet4s

import cats.implicits._
import cats.kernel.Eq

/**
 * Package that contains the definition of FIGlet Fonts and the supporting data structures to convert from FLF files
 */
package object figfont {

  implicit val figFontEq: Eq[FIGfont] =
    Eq.fromUniversalEquals

  implicit val figCharacterEq: Eq[FIGcharacter] =
    Eq.fromUniversalEquals

  implicit val figHeaderEq: Eq[FIGheader] =
    Eq.fromUniversalEquals

  implicit val figFontSettingsEq: Eq[FIGfontSettings] =
    Eq.fromUniversalEquals

  implicit val subColumnsEq: Eq[SubColumns] =
    (x: SubColumns, y: SubColumns) => x.value === y.value

  implicit def seqEq[A: Eq]: Eq[Seq[A]] =
    (x: Seq[A], y: Seq[A]) => (x zip y).forall { case (a, b) => a === b }

}
