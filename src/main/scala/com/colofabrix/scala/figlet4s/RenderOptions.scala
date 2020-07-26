package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s.api.InternalAPI
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    layout: Option[HorizontalLayout],
    maxWidth: Int,
)

/**
 * Commodity builder for Rendering Options
 */
final class RenderOptionsBuilder private[figlet4s] (
    val text: String = "",
    val font: FigletResult[Option[FIGfont]] = None.validNec,
    val layout: Option[HorizontalLayout] = None,
    val maxWidth: Int = Int.MaxValue,
) {

  //  Text  //

  /** Use the specified Max Width */
  def text(text: String): RenderOptionsBuilder =
    copy(text = text)

  //  Font  //

  /** Use the default FIGfont */
  def defaultFont(): RenderOptionsBuilder =
    copy(font = None.validNec)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): RenderOptionsBuilder = {
    val font = InternalAPI.loadFontInternal(fontName).map(Some(_))
    copy(font = font)
  }

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String): RenderOptionsBuilder = {
    val font = InternalAPI.loadFont(fontPath).map(Some(_))
    copy(font = font)
  }

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): RenderOptionsBuilder =
    copy(font = Some(font).validNec)

  //  Horizontal layout  //

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): RenderOptionsBuilder =
    copy(layout = None)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): RenderOptionsBuilder =
    copy(layout = Some(layout))

  //  Max width  //

  /** Use the default Max Width */
  def defaultMaxWidth(): RenderOptionsBuilder =
    copy(maxWidth = Int.MaxValue)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): RenderOptionsBuilder =
    copy(maxWidth = maxWidth)

  //  Support  //

  private def copy(
      text: String = this.text,
      font: FigletResult[Option[FIGfont]] = this.font,
      layout: Option[HorizontalLayout] = this.layout,
      maxWidth: Int = this.maxWidth,
  ) = {
    new RenderOptionsBuilder(text, font, layout, maxWidth)
  }
}
