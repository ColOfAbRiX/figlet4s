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

  /**
   * Obtains the printing direction starting from the Integer provided
   */
  def apply(value: Int): PrintDirection = withValue(value)

  val values = findValues
}

/**
 * Full layout parameter
 */
sealed abstract class FullLayout(val value: Int) extends IntEnumEntry

object FullLayout extends IntEnum[FullLayout] {

  /**
   * Apply horizontal smushing rule 1 when smushing
   *
   * Two sub-characters are smushed into a single sub-character if they are the same. This rule does not smush
   * hardblanks.
   */
  final case object EqualCharacterHorizontalSmushing extends FullLayout(value = 1)

  /**
   * Apply horizontal smushing rule 2 when smushing
   *
   * An underscore ("_") will be replaced by any of: "|", "/", "\", "[", "]", "{", "}", "(", ")", "<" or ">".
   */
  final case object UnderscoreHorizontalSmushing extends FullLayout(value = 2)

  /**
   * Apply horizontal smushing rule 3 when smushing
   *
   * A hierarchy of six classes is used: "|", "/\", "[]", "{}", "()", and "<>". When two smushing sub-characters are
   * from different classes, the one from the latter class will be used.
   */
  final case object HierarchyHorizontalSmushing extends FullLayout(value = 4)

  /**
   * Apply horizontal smushing rule 4 when smushing
   *
   * Smushes opposing brackets ("[]" or "]["), braces ("{}" or "}{") and parentheses ("()" or ")(") together, replacing
   * any such pair with a vertical bar ("|").
   */
  final case object OppositePairHorizontalSmushing extends FullLayout(value = 8)

  /**
   * Apply horizontal smushing rule 5 when smushing
   *
   * Smushes "/\" into "|", "\/" into "Y", and "><" into "X". Note that "<>" is not smushed in any way by this rule. The
   * name "BIG X" is historical; originally all three pairs were smushed into "X".
   */
  final case object BigXHorizontalSmushing extends FullLayout(value = 16)

  /**
   * Apply horizontal smushing rule 6 when smushing
   *
   * Smushes two hardblanks together, replacing them with a single hardblank.
   */
  final case object HardblankHorizontalSmushing extends FullLayout(value = 32)

  /**
   * Horizontal fitting (kerning) by default
   */
  final case object UseHorizontalFitting extends FullLayout(value = 64)

  /**
   * Horizontal smushing by default (Overrides 64)
   */
  final case object UseHorizontalSmushing extends FullLayout(value = 128)

  /**
   * Apply vertical smushing rule 1 when smushing
   *
   * Same as horizontal smushing rule 1
   */
  final case object EqualCharacterVerticalSmushing extends FullLayout(value = 256)

  /**
   * Apply vertical smushing rule 2 when smushing
   *
   * Same as horizontal smushing rule 2
   */
  final case object UnderscoreVerticalSmushing extends FullLayout(value = 512)

  /**
   * Apply vertical smushing rule 3 when smushing
   *
   * Same as horizontal smushing rule 3
   */
  final case object HierarchyVerticalSmushing extends FullLayout(value = 1024)

  /**
   * Apply vertical smushing rule 4 when smushing
   *
   * Smushes stacked pairs of "-" and "_", replacing them with a single "=" sub-character. It does not matter which is
   * found above the other. Note that vertical smushing rule 1 will smush IDENTICAL pairs of horizontal lines, while
   * this rule smushes horizontal lines consisting of DIFFERENT sub-characters.
   */
  final case object HorizontalLineVerticalSmushing extends FullLayout(value = 2048)

  /**
   * Apply vertical smushing rule 5 when smushing
   *
   * This one rule is different from all others, in that it "supersmushes" vertical lines consisting of several vertical
   * bars ("|"). This creates the illusion that FIGcharacters have slid vertically against each other. Supersmushing
   * continues until any sub-characters other than "|" would have to be smushed. Supersmushing can produce impressive
   * results, but it is seldom possible, since other sub-characters would usually have to be considered for smushing as
   * soon as any such stacked vertical lines are encountered.
   */
  final case object VerticalLineVerticalSuperSmushing extends FullLayout(value = 4096)

  /**
   * Vertical fitting by default
   */
  final case object UseVerticalFitting extends FullLayout(value = 8192)

  /**
   * Vertical smushing by default (Overrides 8192)
   */
  final case object UseVerticalSmushing extends FullLayout(value = 16384)

  /**
   * Obtains the list of requested setting starting from the Integer provided
   */
  def apply(requestedSettings: Int): Vector[FullLayout] =
    values
      .flatMap { setting =>
        if ((setting.value.toBitSet & requestedSettings.toBitSet).nonEmpty) Vector(setting)
        else Vector()
      }

  val values = findValues.toVector

  /**
   * Check if it's Full Width layout
   */
  def isFullWidth(settings: Vector[FullLayout]): Boolean =
    !settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing)

  /**
   * Check if Horizontal Fitting is enabled in the given settings
   */
  def isHorizontalFitting(settings: Vector[FullLayout]): Boolean =
    settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing)

  /**
   * Check if Controlled Horizontal Smushing is enabled in the given settings
   */
  def isControlledHorizontalSmushing(settings: Vector[FullLayout]): Boolean =
    settings.contains(UseHorizontalSmushing) && (settings intersect horizontalSmushingRules).size != 0

  /**
   * Check if Universal Horizontal Smushing is enabled in the given settings
   */
  def isUniversalHorizontalSmushing(settings: Vector[FullLayout]): Boolean =
    !isControlledHorizontalSmushing(settings)

  /**
   * Check if it's Full Height layout
   */
  def isFullHeight(settings: Vector[FullLayout]): Boolean =
    !settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing)

  /**
   * Check if Vertical Fitting is enabled in the given settings
   */
  def isVerticalFitting(settings: Vector[FullLayout]): Boolean =
    settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing)

  /**
   * Check if Controlled Vertical Smushing is enabled in the given settings
   */
  def isControlledVerticalSmushing(settings: Vector[FullLayout]): Boolean =
    settings.contains(UseVerticalSmushing) && (settings intersect verticalSmushingRules).size != 0

  /**
   * Check if Universal Vertical Smushing is enabled in the given settings
   */
  def isUniversalVerticalSmushing(settings: Vector[FullLayout]): Boolean =
    !isControlledVerticalSmushing(settings)

  private val horizontalSmushingRules = Vector(
    EqualCharacterHorizontalSmushing,
    UnderscoreHorizontalSmushing,
    HierarchyHorizontalSmushing,
    OppositePairHorizontalSmushing,
    BigXHorizontalSmushing,
    HardblankHorizontalSmushing,
  )

  private val verticalSmushingRules = Vector(
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

  /**
   * Full-width layout by default
   *
   * Represents each FIGcharacter occupying the full width or height of its arrangement of sub-characters as designed.
   */
  final case object OldFullWidthLayout extends OldLayout(value = -1)

  /**
   * Horizontal fitting (kerning) layout by default
   *
   * Moves FIGcharacters closer together until they touch. Typographers use the term "kerning" for this phenomenon when
   * applied to the horizontal axis, but fitting also includes this as a vertical behaviour, for which there is
   * apparently no established typographical term.
   */
  final case object OldHorizontalFittingLayout extends OldLayout(value = 0)

  /**
   * Apply horizontal smushing rule 1 when smushing
   *
   * Two sub-characters are smushed into a single sub-character if they are the same. This rule does not smush hardblanks.
   */
  final case object OldEqualCharacterHorizontalSmushing extends OldLayout(value = 1)

  /**
   * Apply horizontal smushing rule 2 when smushing
   *
   * An underscore ("_") will be replaced by any of: "|", "/", "\", "[", "]", "{", "}", "(", ")", "<" or ">".
   */
  final case object OldUnderscoreHorizontalSmushing extends OldLayout(value = 2)

  /**
   * Apply horizontal smushing rule 3 when smushing
   *
   * A hierarchy of six classes is used: "|", "/\", "[]", "{}", "()", and "<>". When two smushing sub-characters are
   * from different classes, the one from the latter class will be used.
   */
  final case object OldHierarchyHorizontalSmushing extends OldLayout(value = 4)

  /**
   * Apply horizontal smushing rule 4 when smushing
   *
   * Smushes opposing brackets ("[]" or "]["), braces ("{}" or "}{") and parentheses ("()" or ")(") together, replacing
   * any such pair with a vertical bar ("|").
   */
  final case object OldOppositePairHorizontalSmushing extends OldLayout(value = 8)

  /**
   * Apply horizontal smushing rule 5 when smushing
   *
   * Smushes "/\" into "|", "\/" into "Y", and "><" into "X". Note that "<>" is not smushed in any way by this rule. The
   * name "BIG X" is historical; originally all three pairs were smushed into "X".
   */
  final case object OldBigXHorizontalSmushing extends OldLayout(value = 16)

  /**
   * Apply horizontal smushing rule 6 when smushing
   *
   * Smushes two hardblanks together, replacing them with a single hardblank.
   */
  final case object OldHardblankHorizontalSmushing extends OldLayout(value = 32)

  /**
   * Obtains the list of requested setting starting from the Integer provided
   */
  def apply(requestedSettings: Int): Vector[OldLayout] =
    if (requestedSettings < 0)
      Vector(OldFullWidthLayout)
    else if (requestedSettings == 0)
      Vector(OldHorizontalFittingLayout)
    else
      values
        .toVector
        .filter(_ != OldHorizontalFittingLayout)
        .flatMap { setting =>
          if ((requestedSettings.toBitSet & setting.value.toBitSet) == setting.value.toBitSet) Vector(setting)
          else Vector()
        }

  val values = findValues

  /**
   * Check if it's Full Width layout
   */
  def isFullWidth(settings: Vector[OldLayout]): Boolean =
    settings.contains(OldFullWidthLayout)

  /**
   * Check if Fitting is enabled in the given settings
   */
  def isFitting(settings: Vector[OldLayout]): Boolean =
    settings.contains(OldHorizontalFittingLayout)

  /**
   * Check if Smushing is enabled in the given settings
   */
  def isSmushing(settings: Vector[OldLayout]): Boolean =
    !settings.contains(OldFullWidthLayout) && !settings.contains(OldHorizontalFittingLayout)
}
