package com.colofabrix.scala.figlet4s.either

import scala.util._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[either] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[FigletEither] with FIGureEffectfulAPI[FigletEither] {
    /**
     * Apply a function to each line of the FIGure
     *
     * @param f The function to applied to each displayable line
     */
    def foreachLine[A](f: String => A): FigletEither[Unit] = Right {
      self.cleanLines.foreach(_.foreach(f))
    }

    /**
     * Print the FIGure to standard output
     */
    def print(): FigletEither[Unit] =
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
      asSeq().mkString("\n")

    /**
     * The figure as a collection of String, one String per displayable line
     *
     * @return A collection of strings, each containing a displayable line
     */
    def asSeqF(): FigletEither[Seq[String]] =
      Right(asSeq())

    /**
     * The figure as single String and newline characters
     *
     * @return A single string containing the FIGure including newlines where needed
     */
    def asStringF(): FigletEither[String] =
      Right(asString())
  }

}
