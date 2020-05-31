package com.colofabrix.scala.figlet4s.figfont.header

import enumeratum.values._
import scala.collection.BitSet

/**
 * Print direction parameter
 */
sealed abstract class PrintDirection(val value: Int, val name: String) extends IntEnumEntry

object PrintDirection extends IntEnum[PrintDirection] {
  /** Print direction left-to-right */
  final case object LeftToRight extends PrintDirection(value = 0, name = "LeftToRight")
  /** Print direction right-to-left */
  final case object RightToLeft extends PrintDirection(value = 1, name = "RightToLeft")

  def apply(value: Int): PrintDirection = withValue(value)

  val values = findValues
}

/**
 * Full layout parameter
 */
sealed abstract class FullLayout(val value: Int, val name: String) extends IntEnumEntry

object FullLayout extends IntEnum[FullLayout] {
  /** Apply horizontal smushing rule 1 when smushing */
  final case object HorizontalSmushingRule1NewLayout extends FullLayout(value = 1, name = "HorizontalSmushingRule1")
  /** Apply horizontal smushing rule 2 when smushing */
  final case object HorizontalSmushingRule2NewLayout extends FullLayout(value = 2, name = "HorizontalSmushingRule2")
  /** Apply horizontal smushing rule 3 when smushing */
  final case object HorizontalSmushingRule3NewLayout extends FullLayout(value = 4, name = "HorizontalSmushingRule3")
  /** Apply horizontal smushing rule 4 when smushing */
  final case object HorizontalSmushingRule4NewLayout extends FullLayout(value = 8, name = "HorizontalSmushingRule4")
  /** Apply horizontal smushing rule 5 when smushing */
  final case object HorizontalSmushingRule5NewLayout extends FullLayout(value = 16, name = "HorizontalSmushingRule5")
  /** Apply horizontal smushing rule 6 when smushing */
  final case object HorizontalSmushingRule6NewLayout extends FullLayout(value = 32, name = "HorizontalSmushingRule6")
  /** Horizontal fitting (kerning) by default */
  final case object HorizontalFittingNewLayout extends FullLayout(value = 64, name = "HorizontalFitting")
  /** Horizontal smushing by default (Overrides 64) */
  final case object HorizontalSmushingNewLayout extends FullLayout(value = 128, name = "HorizontalSmushing")
  /** Apply vertical smushing rule 1 when smushing */
  final case object VerticalSmushingRule1NewLayout extends FullLayout(value = 256, name = "VerticalSmushingRule1")
  /** Apply vertical smushing rule 2 when smushing */
  final case object VerticalSmushingRule2NewLayout extends FullLayout(value = 512, name = "VerticalSmushingRule2")
  /** Apply vertical smushing rule 3 when smushing */
  final case object VerticalSmushingRule3NewLayout extends FullLayout(value = 1024, name = "VerticalSmushingRule3")
  /** Apply vertical smushing rule 4 when smushing */
  final case object VerticalSmushingRule4NewLayout extends FullLayout(value = 2048, name = "VerticalSmushingRule4")
  /** Apply vertical smushing rule 5 when smushing */
  final case object VerticalSmushingRule5NewLayout extends FullLayout(value = 4096, name = "VerticalSmushingRule5")
  /** Vertical fitting by default */
  final case object VerticalFittingNewLayout extends FullLayout(value = 8192, name = "VerticalFitting")
  /** Vertical smushing by default (Overrides 8192) */
  final case object VerticalSmushingNewLayout extends FullLayout(value = 16384, name = "VerticalSmushing")

  def apply(value: Int): Vector[FullLayout] = {
    val bitSet = value
      .toBinaryString
      .reverse
      .zipWithIndex
      .flatMap {
        case ('0', _) => IndexedSeq()
        case ('1', n) => IndexedSeq(n)
        case (_, _)   => IndexedSeq() // Never gets here thanks to .toBinaryString
      }

    BitSet
      .fromSpecific(bitSet)
      .toVector
      .map(findValues)
  }

  val values = findValues

}

/**
 * Old layout parameter
 */
sealed abstract class OldLayout(val value: Int, val name: String) extends IntEnumEntry

object OldLayout extends IntEnum[OldLayout] {
  /** Full-width layout by default */
  final case object FullWidthLayoutOldlayout extends OldLayout(value = -1, name = "FullWidthLayout")
  /** Horizontal fitting (kerning) layout by default */
  final case object KerningLayoutOldlayout extends OldLayout(value = 0, name = "KerningLayout")
  /** Apply horizontal smushing rule 1 by default */
  final case object HorizontalSmushingRule1Oldlayout extends OldLayout(value = 1, name = "HorizontalSmushingRule1")
  /** Apply horizontal smushing rule 2 by default */
  final case object HorizontalSmushingRule2Oldlayout extends OldLayout(value = 2, name = "HorizontalSmushingRule2")
  /** Apply horizontal smushing rule 3 by default */
  final case object HorizontalSmushingRule3Oldlayout extends OldLayout(value = 4, name = "HorizontalSmushingRule3")
  /** Apply horizontal smushing rule 4 by default */
  final case object HorizontalSmushingRule4Oldlayout extends OldLayout(value = 8, name = "HorizontalSmushingRule4")
  /** Apply horizontal smushing rule 5 by default */
  final case object HorizontalSmushingRule5Oldlayout extends OldLayout(value = 16, name = "HorizontalSmushingRule5")
  /** Apply horizontal smushing rule 6 by default */
  final case object HorizontalSmushingRule6Oldlayout extends OldLayout(value = 32, name = "HorizontalSmushingRule6")

  def apply(value: Int): Vector[OldLayout] =
    if (value == -1) {
      Vector(FullWidthLayoutOldlayout)

    } else {
      val bitSet = value
        .toBinaryString
        .reverse
        .zipWithIndex
        .flatMap {
          case ('0', _) => IndexedSeq()
          case ('1', n) => IndexedSeq(n)
          case (_, _)   => IndexedSeq() // Never gets here thanks to .toBinaryString
        }

      BitSet
        .fromSpecific(bitSet)
        .toVector
        .map(findValues)
    }

  val values = findValues
}
