package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[unsafe] trait FIGureOps {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[Id] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /** Print the FIGure */
    def print(): Unit =
      self.foreachLine(println)

    /** The figure as a Vector of String */
    def asSeq(): Seq[String] =
      self.cleanLines.flatMap(_.value)

    /** The figure as single String */
    def asString(): String =
      asSeq().mkString("\n")
  }

}
