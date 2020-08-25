package com.colofabrix.scala.figlet4s.unsafeops

import cats._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[figlet4s] trait FIGureOps {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[Id] with FIGureEffectfulAPI[Id] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /** Print the FIGure */
    def print(): Unit =
      self.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): Vector[String] =
      self.cleanLines.flatMap(_.value)

    /** The figure as a collection of String, one String per displayed line */
    def asVectorF(): Id[Vector[String]] =
      asVector()

    /** The figure as single String */
    def asString(): String =
      asVector().mkString("\n")

    /** The figure as single String and newline characters */
    def asStringF(): Id[String] =
      asString()
  }

}
