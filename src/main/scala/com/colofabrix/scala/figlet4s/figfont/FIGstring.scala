package com.colofabrix.scala.figlet4s.figfont

final case class FIGstring(value: Vector[FIGcharacter], font: FIGfont) {
  def empty: FIGstring =
    this.copy(value = Vector.empty)

  def append(that: FIGcharacter): FIGstring =
    this.copy(value = this.value :+ that)

  def append(that: FIGstring): Option[FIGstring] =
    Option.when(this.font == that.font)(this.copy(value = this.value ++ that.value))

  def map(f: String => String): FIGstring =
    this.copy(value = decomposeString(f(this.toString)))

  def flatMap(f: String => FIGstring): FIGstring =
    f(this.toString)

  def decomposeString(value: String): Vector[FIGcharacter] =
    value.toVector.map(font(_))

  override lazy val toString: String =
    value.map(_.name).mkString
}

final object FIGstring {
  def apply(value: Vector[FIGcharacter], font: FIGfont): FIGstring = ???

  def apply(font: FIGfont): FIGstring =
    FIGstring(Vector.empty, font)

  def apply(value: String, font: FIGfont): FIGstring =
    FIGstring(value.toVector.map(font(_)), font)
}
