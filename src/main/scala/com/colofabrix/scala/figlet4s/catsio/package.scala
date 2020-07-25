package com.colofabrix.scala.figlet4s

import cats.effect._
import com.colofabrix.scala.figlet4s.figfont._

package object catsio {

  implicit class RenderOptionsBuilderOps(val self: RenderOptionsBuilder) extends AnyVal {
    /**
     * Renders a given text as a FIGure inside Cat's IO monad
     */
    def unsafeRender(): IO[FIGure] =
      for {
        font     <- self.font.fold(e => IO.raiseError(e.head), IO(_))
        safeFont <- font.map(IO(_)).getOrElse(Figlet4s.loadFontInternal())
        options  <- buildOptions(safeFont)
        rendered <- Figlet4s.renderString(self.text, options)
      } yield rendered

    //  Support  //

    private def buildOptions(font: FIGfont): IO[RenderOptions] =
      IO.pure {
        RenderOptions(
          font = font,
          layout = self.layout,
          maxWidth = self.maxWidth,
        )
      }
  }

}
