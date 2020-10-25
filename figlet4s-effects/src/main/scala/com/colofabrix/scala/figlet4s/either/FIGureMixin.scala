package com.colofabrix.scala.figlet4s.either

import scala.util._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[either] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[FigletEither] with FIGureEffectfulAPI[FigletEither] {
    /** @inheritdoc */
    def foreachLine[A](f: String => A): FigletEither[Unit] = Right {
      self.cleanLines.foreach(_.foreach(f))
    }

    /** @inheritdoc */
    def print(): FigletEither[Unit] =
      self.foreachLine(println)

    /** @inheritdoc */
    def asSeq(): Seq[String] =
      self.cleanLines.flatMap(_.value)

    /** @inheritdoc */
    def asString(): String =
      asSeq().mkString("\n")

    /** @inheritdoc */
    def asSeqF(): FigletEither[Seq[String]] =
      Right(asSeq())

    /** @inheritdoc */
    def asStringF(): FigletEither[String] =
      Right(asString())
  }

}
