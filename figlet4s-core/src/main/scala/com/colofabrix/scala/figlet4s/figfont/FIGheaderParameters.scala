package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import cats.kernel.Eq
import com.colofabrix.scala.figlet4s.compat._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.utils._
import com.colofabrix.scala.figlet4s.validatedCompat._
import enumeratum.values._
import scala.util._

/**
 * Parameters and configuration settings used by FIGheaders.
 *
 * These settings are a one-to-one mapping from the FLF format and not suited for internal higher-level processing. As
 * such they shouldn't be used internally and they're here only to represent the FLF header.
 */
private[figfont] object FIGheaderParameters {

  /**
   * Print direction parameter
   *
   * @param value The numeric value of the Print Direction, contained in the FLF header
   */
  sealed abstract class PrintDirection(val value: Int) extends IntEnumEntry with ADT

  object PrintDirection extends IntEnum[PrintDirection] {

    /** Print direction left-to-right */
    case object LeftToRight extends PrintDirection(value = 0)

    /** Print direction right-to-left */
    case object RightToLeft extends PrintDirection(value = 1)

    /**
     * Obtains the printing direction starting from the Integer provided
     *
     * @param value The number representing the PrintDirection
     * @return The PrintDirection that corresponds to the given numeric value
     */
    def apply(value: Int): FigletResult[PrintDirection] =
      Try(withValue(value))
        .toFigletResult
        .leftMap(_.map(x => FIGheaderError(x.getMessage)))

    val values: Vector[PrintDirection] = findValues.toVector

  }

  /**
   * Full layout parameter
   *
   * @param value The numeric value of the Full Layout, contained in the FLF header
   */
  sealed abstract class FullLayout(val value: Int) extends IntEnumEntry with ADT

  object FullLayout extends IntEnum[FullLayout] {

    case object EqualCharacterHorizontalSmushing extends FullLayout(value = 1)
    case object UnderscoreHorizontalSmushing     extends FullLayout(value = 2)
    case object HierarchyHorizontalSmushing      extends FullLayout(value = 4)
    case object OppositePairHorizontalSmushing   extends FullLayout(value = 8)
    case object BigXHorizontalSmushing           extends FullLayout(value = 16)
    case object HardblankHorizontalSmushing      extends FullLayout(value = 32)
    case object HorizontalFitting                extends FullLayout(value = 64)
    case object HorizontalSmushing               extends FullLayout(value = 128)
    case object EqualCharacterVerticalSmushing   extends FullLayout(value = 256)
    case object UnderscoreVerticalSmushing       extends FullLayout(value = 512)
    case object HierarchyVerticalSmushing        extends FullLayout(value = 1024)
    case object HorizontalLineVerticalSmushing   extends FullLayout(value = 2048)
    case object VerticalLineSupersmushing        extends FullLayout(value = 4096)
    case object VerticalFitting                  extends FullLayout(value = 8192)
    case object VerticalSmushing                 extends FullLayout(value = 16384)

    implicit val oldLayoutEq: Eq[FullLayout] = Eq.fromUniversalEquals

    /**
     * Obtains the list of requested setting starting from the Integer provided
     *
     * @param requestedSettings The number representing the FullLayout
     * @return The FullLayout that corresponds to the given numeric value
     */
    def apply(requestedSettings: Int): FigletResult[Vector[FullLayout]] =
      if (requestedSettings < 0 || requestedSettings > 32767)
        FIGheaderError(s"OldLayout needs a value between 0 and 32767, both included: $requestedSettings").invalidNec
      else {
        lazy val result =
          values
            .flatMap { setting =>
              if ((setting.value.toBitSet & requestedSettings.toBitSet).nonEmpty) Vector(setting)
              else Vector()
            }
        Try(result)
          .toFigletResult
          .leftMap(_.map(x => FIGheaderError(x.getMessage)))
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
      VerticalLineSupersmushing,
    )

  }

  /**
   * Old layout parameter
   *
   * @param value The numeric value of the Old Layout, contained in the FLF header
   */
  sealed abstract class OldLayout(val value: Int) extends IntEnumEntry with ADT

  object OldLayout extends IntEnum[OldLayout] {

    case object FullWidth              extends OldLayout(value = -1)
    case object HorizontalFitting      extends OldLayout(value = 0)
    case object EqualCharacterSmushing extends OldLayout(value = 1)
    case object UnderscoreSmushing     extends OldLayout(value = 2)
    case object HierarchySmushing      extends OldLayout(value = 4)
    case object OppositePairSmushing   extends OldLayout(value = 8)
    case object BigXSmushing           extends OldLayout(value = 16)
    case object HardblankSmushing      extends OldLayout(value = 32)

    implicit val oldLayoutEq: Eq[OldLayout] = Eq.fromUniversalEquals

    /**
     * Obtains the list of requested setting starting from the Integer provided
     *
     * @param requestedSettings The number representing the OldLayout
     * @return The OldLayout that corresponds to the given numeric value
     */
    def apply(requestedSettings: Int): FigletResult[Vector[OldLayout]] =
      if (requestedSettings < -1 || requestedSettings > 63)
        FIGheaderError(s"OldLayout needs a value between -1 and 63, both included: $requestedSettings").invalidNec
      else if (requestedSettings === -1)
        Vector(FullWidth).validNec
      else if (requestedSettings === 0)
        Vector(HorizontalFitting).validNec
      else {
        lazy val result =
          values
            .withFilter(_ =!= HorizontalFitting)
            .flatMap { setting =>
              if ((requestedSettings.toBitSet & setting.value.toBitSet) === setting.value.toBitSet) Vector(setting)
              else Vector()
            }
        Try(result)
          .toFigletResult
          .leftMap(_.map(x => FIGheaderError(x.getMessage)))
      }

    val values: Vector[OldLayout] = findValues.toVector

  }

}
