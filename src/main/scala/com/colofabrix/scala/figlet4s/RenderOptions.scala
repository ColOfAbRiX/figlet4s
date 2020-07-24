package com.colofabrix.scala.figlet4s

import cats.implicits._
import cats.instances.string
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import RenderOptionsBuilder._

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    layout: Option[HorizontalLayout],
    maxWidth: Int,
)

object ImpureBuilder {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit class ImpureBuilderOps(val self: RenderOptionsBuilder) extends AnyVal {
    /**
     * Renders a given text as a FIGure
     */
    def unsafeRender(): FIGure = {
      val font = self
        .font
        .fold(e => throw e.head, identity)
        .getOrElse(Figlet4sUnsafe.loadFontInternal("standard"))

      val options = RenderOptions(
        font = font,
        layout = self.layout,
        maxWidth = self.maxWidth,
      )

      Figlet4sAPI.renderString(self.text, options)
    }
  }

}

/**
 * Commodity builder for Rendering Options
 */
final class RenderOptionsBuilder private[figlet4s] (
    val text: String = "",
    val font: FigletResult[Option[FIGfont]] = None.validNec,
    val layout: Option[HorizontalLayout] = None,
    val maxWidth: Int = Int.MaxValue,
) {
  /** Use the default FIGfont */
  def defaultFont(): RenderOptionsBuilder =
    copy(font = None.validNec)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): RenderOptionsBuilder = {
    val font = Figlet4sAPI.loadFontInternal(fontName).map(Some(_))
    copy(font = font)
  }

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String): RenderOptionsBuilder = {
    val font = Figlet4sAPI.loadFont(fontPath).map(Some(_))
    copy(font = font)
  }

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): RenderOptionsBuilder =
    copy(font = Some(font).validNec)

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): RenderOptionsBuilder =
    copy(layout = None)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): RenderOptionsBuilder =
    copy(layout = Some(layout))

  /** Use the default Max Width */
  def defaultMaxWidth(): RenderOptionsBuilder =
    copy(maxWidth = Int.MaxValue)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): RenderOptionsBuilder =
    copy(maxWidth = maxWidth)

  /** Use the specified Max Width */
  def text(text: String): RenderOptionsBuilder =
    copy(text = text)

  private def copy(
      text: String = this.text,
      font: FigletResult[Option[FIGfont]] = this.font,
      layout: Option[HorizontalLayout] = this.layout,
      maxWidth: Int = this.maxWidth,
  ) = {
    new RenderOptionsBuilder(text, font, layout, maxWidth)
  }
}

object RenderOptionsBuilder {
  def apply(): RenderOptionsBuilder =
    new RenderOptionsBuilder()

  def apply(text: String): RenderOptionsBuilder =
    new RenderOptionsBuilder(text = text)
}
