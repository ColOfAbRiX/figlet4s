package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import cats.kernel.Eq
import com.colofabrix.scala.figlet4s.utils._
import enumeratum.values._

/**
 * Parameters and configuration settings used by FIGheaders.
 *
 * These settings are a one-to-one mapping from the FLF format and not suited for internal higher-level processing. As
 * such they shouldn't be used internally and they're here only to represent the FLF header.
 */
private[figfont] object FIGheaderParameters {
  /**
   * Print direction parameter
   */
  sealed abstract class PrintDirection(val value: Int) extends IntEnumEntry

  object PrintDirection extends IntEnum[PrintDirection] {
    /** Print direction left-to-right */
    final case object LeftToRight extends PrintDirection(value = 0)
    /** Print direction right-to-left */
    final case object RightToLeft extends PrintDirection(value = 1)

    /**
     * Obtains the printing direction starting from the Integer provided
     */
    def apply(value: Int): PrintDirection = withValue(value)

    val values: Vector[PrintDirection] = findValues.toVector
  }

  /**
   * Full layout parameter
   */
  sealed abstract class FullLayout(val value: Int) extends IntEnumEntry with ADT

  object FullLayout extends IntEnum[FullLayout] {

    final case object EqualCharacterHorizontalSmushing  extends FullLayout(value = 1)
    final case object UnderscoreHorizontalSmushing      extends FullLayout(value = 2)
    final case object HierarchyHorizontalSmushing       extends FullLayout(value = 4)
    final case object OppositePairHorizontalSmushing    extends FullLayout(value = 8)
    final case object BigXHorizontalSmushing            extends FullLayout(value = 16)
    final case object HardblankHorizontalSmushing       extends FullLayout(value = 32)
    final case object UseHorizontalFitting              extends FullLayout(value = 64)
    final case object UseHorizontalSmushing             extends FullLayout(value = 128)
    final case object EqualCharacterVerticalSmushing    extends FullLayout(value = 256)
    final case object UnderscoreVerticalSmushing        extends FullLayout(value = 512)
    final case object HierarchyVerticalSmushing         extends FullLayout(value = 1024)
    final case object HorizontalLineVerticalSmushing    extends FullLayout(value = 2048)
    final case object VerticalLineVerticalSuperSmushing extends FullLayout(value = 4096)
    final case object UseVerticalFitting                extends FullLayout(value = 8192)
    final case object UseVerticalSmushing               extends FullLayout(value = 16384)

    implicit val oldLayoutEq: Eq[FullLayout] = Eq.fromUniversalEquals

    /**
     * Obtains the list of requested setting starting from the Integer provided
     */
    def apply(requestedSettings: Int): Vector[FullLayout] =
      values
        .flatMap { setting =>
          if ((setting.value.toBitSet & requestedSettings.toBitSet).nonEmpty) Vector(setting)
          else Vector()
        }

    val values: Vector[FullLayout] = findValues.toVector

    /**
     * The settings that belongs to Horizontal Smushing
     */
    val horizontalSmushingRules: Vector[FullLayout] = Vector(
      EqualCharacterHorizontalSmushing,
      UnderscoreHorizontalSmushing,
      HierarchyHorizontalSmushing,
      OppositePairHorizontalSmushing,
      BigXHorizontalSmushing,
      HardblankHorizontalSmushing,
    )

    /**
     * The settings that belongs to Vertical Smushing
     */
    val verticalSmushingRules: Vector[FullLayout] = Vector(
      EqualCharacterVerticalSmushing,
      UnderscoreVerticalSmushing,
      HierarchyVerticalSmushing,
      HorizontalLineVerticalSmushing,
      VerticalLineVerticalSuperSmushing,
    )
  }

  /**
   * Old layout parameter
   */
  sealed abstract class OldLayout(val value: Int) extends IntEnumEntry

  object OldLayout extends IntEnum[OldLayout] {

    final case object OldFullWidthLayout                  extends OldLayout(value = -1)
    final case object OldHorizontalFittingLayout          extends OldLayout(value = 0)
    final case object OldEqualCharacterHorizontalSmushing extends OldLayout(value = 1)
    final case object OldUnderscoreHorizontalSmushing     extends OldLayout(value = 2)
    final case object OldHierarchyHorizontalSmushing      extends OldLayout(value = 4)
    final case object OldOppositePairHorizontalSmushing   extends OldLayout(value = 8)
    final case object OldBigXHorizontalSmushing           extends OldLayout(value = 16)
    final case object OldHardblankHorizontalSmushing      extends OldLayout(value = 32)

    implicit val oldLayoutEq: Eq[OldLayout] = Eq.fromUniversalEquals

    /**
     * Obtains the list of requested setting starting from the Integer provided
     */
    def apply(requestedSettings: Int): Vector[OldLayout] =
      if (requestedSettings < 0)
        Vector(OldFullWidthLayout)
      else if (requestedSettings === 0)
        Vector(OldHorizontalFittingLayout)
      else
        values
          .filter(_ =!= OldHorizontalFittingLayout)
          .flatMap { setting =>
            if ((requestedSettings.toBitSet & setting.value.toBitSet) === setting.value.toBitSet) Vector(setting)
            else Vector()
          }

    val values: Vector[OldLayout] = findValues.toVector
  }
}
