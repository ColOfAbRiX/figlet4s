package com.colofabrix.scala.figlet4s.unsafe

import cats._
import cats.implicits._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

private[unsafe] trait OptionsBuilderMixin {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[Id] {

    private lazy val buildOptions = self.compile[Id]

    /** @inheritdoc */
    @throws(classOf[FigletError])
    def text: String = buildOptions.text

    /** @inheritdoc */
    @throws(classOf[FigletError])
    def render(): FIGure =
      Figlet4s.renderString(buildOptions.text, options)

    /** @inheritdoc */
    @throws(classOf[FigletError])
    def render(text: String): FIGure =
      self
        .text(text)
        .render()

    /** @inheritdoc */
    @throws(classOf[FigletError])
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
