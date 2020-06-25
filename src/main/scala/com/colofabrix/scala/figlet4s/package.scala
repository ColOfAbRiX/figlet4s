package com.colofabrix.scala

import cats.data._
import com.colofabrix.scala.figlet4s.figfont.FIGure

package object figlet4s {

  /**
   * A result of a processing operation that might include errors
   */
  type FigletResult[A] = Validated[NonEmptyChain[FigletError], A]

  implicit class FIGureOps(val figure: FIGure) extends AnyVal {
    import com.colofabrix.scala.figlet4s.figfont.FIGure._

    def foreachLine[A](f: String => A): Unit =
      figure.cleanLines.foreach(_.foreach(f))

    def print(): Unit =
      figure.foreachLine(println)

    def build(): String =
      figure.lines.map(_.value.mkString("\n")).mkString("\n")
  }

}
