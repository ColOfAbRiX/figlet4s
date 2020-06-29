package com.colofabrix.scala.figlet4s.figfont

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGure._

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
  lazy val lastLine: SubLines = lines.last

  /**
   * Lines stripped of their hardblanks
   */
  val cleanLines: Vector[SubLines] =
    lines
      .map(_.replace(font.header.hardblank.toString, " "))

  /**
   * Appends the last line of "that" FIGure to the last line of this FIGure
   */
  def append(that: FIGure): FIGure =
    this.copy(
      value = this.value + that.value,
      lines = this.lastLine +: Vector(that.lastLine),
    )

  /**
   * Replace the last line of this FIGure with the last line of "that" FIGure
   */
  def replace(value: String, line: SubLines): FIGure =
    this.copy(
      lines = this.lines.dropRight(1) ++ Vector(line),
      value = this.value + value,
    )

  def zipLinesWith(that: FIGure)(f: (String, String) => String): SubLines = {
    val processed = (this.lastLine.value zip that.lastLine.value).map(f.tupled)
    SubLines(processed)
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
