package com.colofabrix.scala.figlet4s.unsafe

import cats._
import cats.implicits._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.core._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.validatedCompat._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[unsafe] trait OptionsBuilderMixin {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[Id] {

    private lazy val buildOptions = self.compile[Id]

    /**
     * The text to render
     *
     * @return The text to render as String
     */
    @throws(classOf[FigletException])
    def text: String = buildOptions.text

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @return A FIGure representing the rendered text
     */
    @throws(classOf[FigletException])
    def render(): FIGure =
      Figlet4s.renderString(buildOptions.text, options)

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @param text The text to render
     * @return A FIGure representing the rendered text
     */
    @throws(classOf[FigletException])
    def render(text: String): FIGure =
      self
        .text(text)
        .render()

    /**
     * Builds and returns the render options
     *
     * @return The RenderOptions resulting from building the internal state
     */
    @throws(classOf[FigletException])
    def options: RenderOptions = {
      lazy val defaultFont = Figlet4sClient.defaultFont

      val font =
        buildOptions
          .font
          .getOrElse(Figlet4s.loadFontInternal(defaultFont).validNec)
          .unsafeGet

      val maxWidth =
        buildOptions
          .maxWidth
          .getOrElse(Figlet4sClient.defaultMaxWidth)

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
