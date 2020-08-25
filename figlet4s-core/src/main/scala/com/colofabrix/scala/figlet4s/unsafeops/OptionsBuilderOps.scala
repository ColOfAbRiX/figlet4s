package com.colofabrix.scala.figlet4s.unsafeops

import cats._
import cats.implicits._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[figlet4s] trait OptionsBuilderOps {
  import SyncId._
  import FigletResultOps._

  @throws(classOf[FigletError])
  implicit class OptionsBuilderOps(val self: OptionsBuilder)
      extends OptionsBuilderAPI[Id]
      with OptionsBuilderEffectfulAPI[Id] {

    private lazy val buildOptions = self.compile[Id]

    /** The text to render */
    def text: String = buildOptions.text

    /** The text to render */
    def textF: String = text

    /** Renders a given text as a FIGure and throws exceptions in case of errors */
    def render(): FIGure =
      Figlet4s.renderString(buildOptions.text, options)

    /** Renders a given text into a FIGure */
    def render(text: String): FIGure =
      self
        .text(text)
        .render()

    /** Renders the text into a FIGure */
    def renderF(): FIGure = render()

    /** Renders a given text into a FIGure */
    def renderF(text: String): FIGure = render(text)

    /** Builds an instance of RenderOptions */
    def options: RenderOptions = {
      val font =
        buildOptions
          .font
          .getOrElse(Figlet4s.loadFontInternal().validNec)
          .unsafeGet

      val maxWidth =
        buildOptions
          .maxWidth
          .getOrElse(Int.MaxValue)

      RenderOptions(
        font,
        maxWidth,
        buildOptions.horizontalLayout,
        buildOptions.printDirection,
        buildOptions.justification,
      )
    }
  }

}
