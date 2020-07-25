package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont.FIGure

package object catsio {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit class RenderOptionsBuilderOps(val self: RenderOptionsBuilder) extends AnyVal {
    /**
     * Renders a given text as a FIGure and throws exceptions in case of errors
     */
    def unsafeRender(): FIGure = {
      val font = self
        .font
        .fold(e => throw e.head, identity)
        .getOrElse(Figlet4s.loadFontInternal())

      val options = RenderOptions(
        font = font,
        layout = self.layout,
        maxWidth = self.maxWidth,
      )

      Figlet4s.renderString(self.text, options)
    }
  }

}
