package com.colofabrix.scala.figlet4s

import cats.effect._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.utils._

package object catsio {

  implicit class RenderOptionsBuilderOps(val self: RenderOptionsBuilder) extends OptionsBuilderClientAPI[IO] {
    /** The text to render */
    def text: IO[String] = IO.pure(self.text)

    /** Renders the text into a FIGure */
    def render(): IO[FIGure] =
      for {
        options  <- options
        rendered <- InternalAPI.renderString[IO](self.text, options)
      } yield rendered

    /** Returns the render options */
    def options: IO[RenderOptions] =
      for {
        font     <- self.font.asIO
        safeFont <- font.map(IO(_)).getOrElse(defaultFont)
      } yield RenderOptions(safeFont, self.layout, self.maxWidth)

    //  Support  //

    private def defaultFont: IO[FIGfont] =
      InternalAPI.loadFontInternal[IO]().flatMap(_.asIO)
  }

  implicit class FIGureOps(val figure: FIGure) extends FIGureClientAPI[IO] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): IO[Unit] = IO {
      figure.cleanLines.foreach(_.foreach(f))
    }

    /** Print the FIGure */
    def print(): IO[Unit] =
      figure.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): IO[Vector[String]] = IO.pure {
      figure.lines.map(_.value.mkString("\n"))
    }

    /** The figure as single String */
    def asString(): IO[String] =
      asVector().map(_.mkString("\n"))
  }

}
