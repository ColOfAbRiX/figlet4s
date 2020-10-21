package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import com.colofabrix.scala.figlet4s.Figlet4sClient
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[catsio] trait OptionsBuilderMixin {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[IO] {
    private lazy val buildOptions = self.compile[IO]

    /** The text to render */
    def text: IO[String] = buildOptions.map(_.text)

    /** Renders the text into a FIGure */
    def render(): IO[FIGure] =
      for {
        options  <- options
        text     <- text
        rendered <- Figlet4sClient.renderString[IO](text, options)
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
        maxWidth         <- builtMaxWidth
        horizontalLayout <- buildOptions.map(_.horizontalLayout)
        printDirection   <- buildOptions.map(_.printDirection)
        justification    <- buildOptions.map(_.justification)
      } yield {
        RenderOptions(font, maxWidth, horizontalLayout, printDirection, justification)
      }

    //  Support  //

    private def builtFont: IO[FIGfont] =
      for {
        optionFont <- buildOptions.map(_.font.map(_.asIO))
        font       <- optionFont.getOrElse(builtDefaultFont)
      } yield font

    private def builtDefaultFont: IO[FIGfont] =
      Figlet4sClient.loadFontInternal[IO]().flatMap(_.asIO)

    private def builtMaxWidth: IO[Int] =
      for {
        optionMaxWidth <- buildOptions.map(_.maxWidth)
        maxWidth       <- IO.pure(optionMaxWidth.getOrElse(Int.MaxValue))
      } yield maxWidth
  }

}
