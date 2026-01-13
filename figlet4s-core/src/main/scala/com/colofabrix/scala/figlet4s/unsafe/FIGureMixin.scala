package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[unsafe] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[Id] {

    /**
     * Apply a function to each line of the FIGure
     *
     * @param f The function to applied to each displayable line
     */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /**
     * Print the FIGure to standard output
     */
    def print(): Unit =
      self.foreachLine(println)

    /**
     * The figure as a collection of String, one String per displayable line
     *
     * @return A collection of strings, each containing a displayable line
     */
    def asSeq(): Seq[String] =
      self.cleanLines.flatMap(_.value)

    /**
     * The figure as single String and newline characters
     *
     * @return A single string containing the FIGure including newlines where needed
     */
    def asString(): String =
      asSeq().mkString(System.lineSeparator())

  }

}
