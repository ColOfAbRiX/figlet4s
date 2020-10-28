package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import com.colofabrix.scala.figlet4s.Figlet4sClient
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[catsio] trait OptionsBuilderMixin {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[IO] {
    private lazy val buildOptions = self.compile[IO]

    /**
     * The text to render
     *
     * @return The text to render as String
     */
    def text: IO[String] = buildOptions.map(_.text)

    /**
     * Builds and returns the render options
     *
     * @return The RenderOptions resulting from building the internal state
     */
    def render(): IO[FIGure] =
      for {
        options  <- options
        text     <- text
        rendered <- Figlet4sClient.renderString[IO](text, options)
      } yield rendered

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @return A FIGure representing the rendered text
     */
    def render(text: String): IO[FIGure] =
      self
        .text(text)
        .render()

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @param text The text to render
     * @return A FIGure representing the rendered text
     */
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
