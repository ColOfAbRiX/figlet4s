package com.colofabrix.scala.figlet4s.either

import scala.util._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[either] trait OptionsBuilderMixin {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[FigletEither] {
    private lazy val buildOptions = self.compile[FigletEither]

    /** @inheritdoc */
    def text: FigletEither[String] = buildOptions.map(_.text)

    /** @inheritdoc */
    def render(): FigletEither[FIGure] =
      for {
        options  <- options
        text     <- text
        rendered <- Figlet4sClient.renderString[FigletEither](text, options)
      } yield rendered

    /** @inheritdoc */
    def render(text: String): FigletEither[FIGure] =
      self
        .text(text)
        .render()

    /** @inheritdoc */
    def options: FigletEither[RenderOptions] =
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

    private def builtFont: FigletEither[FIGfont] =
      for {
        optionFont <- buildOptions.map(_.font.map(_.asEither))
        font       <- optionFont.getOrElse(builtDefaultFont)
      } yield font

    private def builtDefaultFont: FigletEither[FIGfont] =
      Figlet4sClient
        .loadFontInternal[FigletEither](Figlet4sClient.defaultFont)
        .flatMap(_.asEither)

    private def builtMaxWidth: FigletEither[Int] =
      for {
        optionMaxWidth <- buildOptions.map(_.maxWidth)
        maxWidth       <- Right(optionMaxWidth.getOrElse(Int.MaxValue))
      } yield maxWidth
  }

}
