package com.colofabrix.scala.figlet4s.figfont

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGure._
import cats.data.State

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    lines: Vector[SubLines],
) {
  /**
   * The FIGure represented with columns
   */
  lazy val columns: Vector[SubColumns] = lines.map(_.toSubcolumns)

  /**
   * The width of the FIGure
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  val width: Int = lines.head.value.head.size

  /**
   * Cached access to the last line of the FIGure as SubLines
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  lazy val lastLines: SubLines = lines.last

  /**
   * Cached access to the last line of the FIGure as SubColumns
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  lazy val lastColumns: SubColumns = columns.last

  /**
   * Lines stripped of their hardblanks
   */
  val cleanLines: Vector[SubLines] =
    lines
      .map(_.replace(font.header.hardblank.toString, " "))

  private[figlet4s] def append(that: FIGure): FIGure =
    this.copy(
      value = this.value + that.value,
      lines = this.lines :+ that.lastLines,
    )

  private[figlet4s] def replace(that: FIGure): FIGure =
    this.copy(
      value = this.value + that.value,
      lines = this.lines.dropRight(1) :+ that.lastLines,
    )

  def appendMappedLines(that: FIGure)(f: (String, String) => String): FIGure = {
    val zipped    = this.lastLines.value zip that.lastLines.value
    val processed = zipped.map(f.tupled)
    this.copy(
      value = this.value + value,
      lines = this.lines.dropRight(1) ++ Vector(SubLines(processed)),
    )
  }
}

object FIGure {
  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Vector(char.lines))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Vector(font(char).lines))
}
