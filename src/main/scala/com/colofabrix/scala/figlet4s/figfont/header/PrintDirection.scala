package com.colofabrix.scala.figlet4s.figfont.header

import enumeratum.values._

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
