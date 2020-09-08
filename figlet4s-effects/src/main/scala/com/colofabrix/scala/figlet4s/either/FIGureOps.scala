package com.colofabrix.scala.figlet4s.either

import scala.util._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[either] trait FIGureOps {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[FigletEither] with FIGureEffectfulAPI[FigletEither] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): FigletEither[Unit] = Right {
      self.cleanLines.foreach(_.foreach(f))
    }

    /** Print the FIGure */
    def print(): FigletEither[Unit] =
      self.foreachLine(println)

    /** The figure as a Vector of String */
    def asSeq(): Vector[String] =
      self.cleanLines.flatMap(_.value)

    /** The figure as single String */
    def asString(): String =
      asSeq().mkString("\n")

    /** The figure as a collection of String, one String per displayed line */
    def asVectorF(): FigletEither[Vector[String]] =
      Right(asSeq())

    /** The figure as single String and newline characters */
    def asStringF(): FigletEither[String] =
      Right(asString())
  }

}
