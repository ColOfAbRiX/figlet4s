package com.colofabrix.scala.figlet4s.figfont

/**
 * Represents the SubLines in Figlet which are the String that compose each line of the FIGure or of a FIGcharacter
 *
 * @param value The collection of lines that compose the SubLines
 */
final case class SubLines(value: Seq[String]) extends SubElementOps[SubLines] {
  protected def pure(value: Seq[String]): SubLines =
    SubLines(value)

  /** The number of columns that compose this SubLines */
  lazy val width: Int =
    value.map(_.length).headOption.getOrElse(0)

  /** Transforms this SubLines representation into a SubColumns */
  lazy val toSubcolumns: SubColumns =
    SubColumns(value.transpose.map(_.mkString))

  override def toString: String =
    value.map("|" + _ + "|").mkString("\n")
}

object SubLines {
  /**
   * A zero-height SubLines object of the specified height
   *
   * @param height The number of lines that compose this SubLines object
   */
  def zero(height: Int): SubLines =
    SubLines(Vector.fill(height)(""))
}

/**
 * Represents the SubColumns in Figlet which are the String that compose each column of the FIGure or of a FIGcharacter
 *
 * @param value The collection of columns that compose the SubColumns
 */
final case class SubColumns(value: Seq[String]) extends SubElementOps[SubColumns] {
  protected def pure(value: Seq[String]): SubColumns =
    SubColumns(value)

  /** The number of lines that compose this SubColumns */
  lazy val height: Int =
    value.map(_.length).headOption.getOrElse(0)

  /** Transforms this SubColumns representation into a SubLines */
  lazy val toSublines: SubLines =
    SubLines(value.transpose.map(_.mkString))

  override def toString: String =
    value.map(_.toVector).transpose.map(_.mkString("|", "", "|")).mkString("\n")
}

object SubColumns {
  /**
   * A one-column SubColumns object of the specified height
   *
   * @param height The number of lines that compose this SubColumns object
   */
  def zero(height: Int): SubColumns =
    SubColumns(Vector(" " * height))
}

/**
 * Operations for SubElements
 */
trait SubElementOps[+A <: SubElementOps[A]] {
  protected def pure(value: Seq[String]): A

  def value: Seq[String]

  /** The length of the SubElement */
  def length: Int =
    value.length

  /** Applies a function to each element to obtain a new SubElement */
  def map(f: String => String): A =
    pure(value.map(f))

  /** Applies a function to each element */
  def foreach[U](f: String => U): Unit =
    value.foreach(f)

  /** Replaces a String value looking inside each element of the SubElement */
  def replace(oldValue: String, newValue: String): A =
    pure(value.map(_.replace(oldValue, newValue)))
}
