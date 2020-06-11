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

    def print(): Unit =
      figure.foreachLine(println)

    def foreachFIGline[A](f: FIGline => A): Unit =
      figure.cleanLines.foreach(f)

    def foreachLine[A](f: String => A): Unit =
      figure.cleanLines.foreach(_.foreach(f))
  }

}
