package com.colofabrix.scala.figlet4s.figfont

import enumeratum.values._
import com.colofabrix.scala.figlet4s.Utils._

/**
 * Print direction parameter
 */
sealed abstract class PrintDirection(val value: Int) extends IntEnumEntry

object PrintDirection extends IntEnum[PrintDirection] {
  /** Print direction left-to-right */
  final case object LeftToRight extends PrintDirection(value = 0)
  /** Print direction right-to-left */
  final case object RightToLeft extends PrintDirection(value = 1)

  def apply(value: Int): PrintDirection = withValue(value)

  val values = findValues
}

/**
 * Full layout parameter
 */
sealed abstract class FullLayout(val value: Int) extends IntEnumEntry

object FullLayout extends IntEnum[FullLayout] {
  /** Apply horizontal smushing rule 1 when smushing */
  final case object EqualCharacterHorizontalSmushing extends FullLayout(value = 1)
  /** Apply horizontal smushing rule 2 when smushing */
  final case object UnderscoreHorizontalSmushing extends FullLayout(value = 2)
  /** Apply horizontal smushing rule 3 when smushing */
  final case object HierarchyHorizontalSmushing extends FullLayout(value = 4)
  /** Apply horizontal smushing rule 4 when smushing */
  final case object OppositePairHorizontalSmushing extends FullLayout(value = 8)
  /** Apply horizontal smushing rule 5 when smushing */
  final case object BigXHorizontalSmushing extends FullLayout(value = 16)
  /** Apply horizontal smushing rule 6 when smushing */
  final case object HardblankHorizontalSmushing extends FullLayout(value = 32)
  /** Horizontal fitting (kerning) by default */
  final case object UseHorizontalFitting extends FullLayout(value = 64)
  /** Horizontal smushing by default (Overrides 64) */
  final case object UseHorizontalSmushing extends FullLayout(value = 128)
  /** Apply vertical smushing rule 1 when smushing */
  final case object EqualCharacterVerticalSmushing extends FullLayout(value = 256)
  /** Apply vertical smushing rule 2 when smushing */
  final case object UnderscoreVerticalSmushing extends FullLayout(value = 512)
  /** Apply vertical smushing rule 3 when smushing */
  final case object HierarchyVerticalSmushing extends FullLayout(value = 1024)
  /** Apply vertical smushing rule 4 when smushing */
  final case object HorizontalLineVerticalSmushing extends FullLayout(value = 2048)
  /** Apply vertical smushing rule 5 when smushing */
  final case object VerticalLineSuperVerticalSmushing extends FullLayout(value = 4096)
  /** Vertical fitting by default */
  final case object UseVerticalFitting extends FullLayout(value = 8192)
  /** Vertical smushing by default (Overrides 8192) */
  final case object UseVerticalSmushing extends FullLayout(value = 16384)

  def apply(requestedSettings: Int): Vector[FullLayout] =
    findValues
      .toVector
      .flatMap { setting =>
        if ((setting.value.toBitSet & requestedSettings.toBitSet).nonEmpty) Vector(setting)
        else Vector()
      }

  val values = findValues
}

/**
 * Old layout parameter
 */
sealed abstract class OldLayout(val value: Int) extends IntEnumEntry

object OldLayout extends IntEnum[OldLayout] {
  /** Full-width layout by default */
  final case object OldFullWidthLayout extends OldLayout(value = -1)
  /** Horizontal fitting (kerning) layout by default */
  final case object OldKerningLayout extends OldLayout(value = 0)
  /** Apply horizontal smushing rule 1 when smushing */
  final case object OldEqualCharacterHorizontalSmushing extends OldLayout(value = 1)
  /** Apply horizontal smushing rule 2 when smushing */
  final case object OldUnderscoreHorizontalSmushing extends OldLayout(value = 2)
  /** Apply horizontal smushing rule 3 when smushing */
  final case object OldHierarchyHorizontalSmushing extends OldLayout(value = 4)
  /** Apply horizontal smushing rule 4 when smushing */
  final case object OldOppositePairHorizontalSmushing extends OldLayout(value = 8)
  /** Apply horizontal smushing rule 5 when smushing */
  final case object OldBigXHorizontalSmushing extends OldLayout(value = 16)
  /** Apply horizontal smushing rule 6 when smushing */
  final case object OldHardblankHorizontalSmushing extends OldLayout(value = 32)

  def apply(requestedSettings: Int): Vector[OldLayout] = {
    if (requestedSettings < 0) {
      Vector(FullWidthLayoutOldlayout)
    } else {
      findValues
        .toVector
        .flatMap { setting =>
          if ((requestedSettings.toBitSet & setting.value.toBitSet) == setting.value.toBitSet) Vector(setting)
          else Vector()
        }
    }
  }

  val values = findValues
}
