package com.colofabrix.scala.figlet4s.figfont

/**
 * Represents the SubLines in Figlet which are the String that compose each line of the FIGure or of a FIGcharacter
 */
final case class SubLines(value: Vector[String]) extends SubElementOps[SubLines] {
  protected def pure(value: Vector[String]): SubLines =
    SubLines(value)

  lazy val toSubcolumns: SubColumns =
    SubColumns(value.transpose.map(_.mkString))

  override def toString(): String =
    value.mkString
}

object SubLines {
  def zero(height: Int): SubLines =
    SubLines(Vector.fill(height)(""))
}

/**
 * Represents the SubColumns in Figlet which are the String that compose each column of the FIGure or of a FIGcharacter
 */
final case class SubColumns(value: Vector[String]) extends SubElementOps[SubColumns] {
  protected def pure(value: Vector[String]): SubColumns =
    SubColumns(value)

  lazy val toSublines: SubLines =
    SubLines(value.transpose.map(_.mkString))

  override def toString(): String =
    value.map(_.toVector).transpose.map(_.mkString("|", "", "|")).mkString("\n")
}

object SubColumns {
  def zero(height: Int): SubColumns =
    SubColumns(Vector(" " * height))
}

/**
 * Operations for SubElements
 */
trait SubElementOps[A <: SubElementOps[A]] {
  protected def pure(value: Vector[String]): A

  def value: Vector[String]

  def length: Int =
    value.length

  def map(f: String => String): A =
    pure(value.map(f))

  def foreach[U](f: String => U): Unit =
    value.foreach(f)

  def replace(oldValue: String, newValue: String): A =
    pure(value.map(_.replace(oldValue, newValue)))
}
