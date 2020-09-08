package com.colofabrix.scala.figlet4s

import cats.implicits._
import cats.kernel.Eq

package object figfont {

  implicit val figFontEq: Eq[FIGfont] = Eq.fromUniversalEquals

  implicit val figCharacterEq: Eq[FIGcharacter] = Eq.fromUniversalEquals

  implicit val figHeaderEq: Eq[FIGheader] = Eq.fromUniversalEquals

  implicit val figFontSettingsEq: Eq[FIGfontSettings] = Eq.fromUniversalEquals

  implicit val figureEq: Eq[FIGure] = new Eq[FIGure] {
    def eqv(x: FIGure, y: FIGure): Boolean = x.cleanColumns === y.cleanColumns
  }

  implicit val subColumnsEq: Eq[SubColumns] = new Eq[SubColumns] {
    def eqv(x: SubColumns, y: SubColumns): Boolean = x.value === y.value
  }

  implicit val subLinesEq: Eq[SubLines] = new Eq[SubLines] {
    def eqv(x: SubLines, y: SubLines): Boolean = x.value === y.value
  }

  implicit def seqEq[A: Eq]: Eq[Seq[A]] = new Eq[Seq[A]] {
    def eqv(x: Seq[A], y: Seq[A]): Boolean = (x zip y).forall { case (a, b) => a === b }
  }

}
