package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont.FIGfont

/**
 * A FIGlet String
 */
final case class FIGstring(value: String, font: FIGfont) {
  @inline final def map(f: String => String): FIGstring =
    FIGstring(f(value), font)

  @inline final def flatMap(f: String => FIGstring): FIGstring =
    f(value)

  @inline final def foreach(f: String => Unit): Unit =
    f(value)

  @inline final def empty: FIGstring =
    FIGstring("", font)

  @inline final def +(char: Char): FIGstring =
    FIGstring(value + char, font)
}

// object FIGstring {
//   def apply(font: FIGfont): FIGstring = empty(font)
//   def empty(font: FIGfont): FIGstring = FIGstring("", font)
// }
