package com.colofabrix.scala.figlet4s

import cats.effect._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

package object catsio {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderClientAPI[IO] {
    private val buildOptions = self.compile[IO]

    /** The text to render */
    def text: IO[String] = buildOptions.map(_.text)

    /** Renders the text into a FIGure */
    def render(): IO[FIGure] =
      for {
        options  <- options
        text     <- text
        rendered <- InternalAPI.renderString[IO](text, options)
      } yield rendered

    /** Renders a given text into a FIGure */
    def render(text: String): IO[FIGure] =
      self
        .text(text)
        .render()

    /** Returns the render options */
    def options: IO[RenderOptions] =
      for {
        font             <- builtFont
        horizontalLayout <- builtHorizontalLayout
        maxWidth         <- builtMaxWidth
        printDirection   <- builtPrintDirection
      } yield {
        RenderOptions(font, maxWidth, horizontalLayout, printDirection)
      }

    //  Support  //

    private def builtFont: IO[FIGfont] =
      for {
        optionFont <- buildOptions.map(_.font.map(_.asIO))
        font       <- optionFont.getOrElse(builtDefaultFont)
      } yield font

    private def builtDefaultFont: IO[FIGfont] =
      InternalAPI.loadFontInternal[IO]().flatMap(_.asIO)

    private def builtHorizontalLayout: IO[FIGfontParameters.HorizontalLayout] =
      for {
        font          <- builtFont
        optionHLayout <- buildOptions.map(_.horizontalLayout)
        hLayout       <- IO.pure(optionHLayout.getOrElse(font.hLayout))
      } yield hLayout

    private def builtMaxWidth: IO[Int] =
      for {
        optionMaxWidth <- buildOptions.map(_.maxWidth)
        maxWidth       <- IO.pure(optionMaxWidth.getOrElse(Int.MaxValue))
      } yield maxWidth

    private def builtPrintDirection: IO[PrintDirection] =
      for {
        optionDirection <- buildOptions.map(_.printDirection)
        printDirection  <- IO.pure(optionDirection.getOrElse(PrintDirection.LeftToRight))
      } yield printDirection
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

  implicit private[catsio] class FigletResultOps[E, A](val self: FigletResult[A]) extends AnyVal {
    /** Transforms the Validated into a Cat's IO capturing the first error in IO */
    def asIO: IO[A] = self.fold(e => IO.raiseError(e.head), IO(_))
  }

}
