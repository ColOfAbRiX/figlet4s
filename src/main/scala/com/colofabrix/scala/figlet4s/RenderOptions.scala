package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.Figlet4s
import RenderOptionsBuilder._

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
class RenderOptionsBuilder private (private val options: BuildData) {
  /**
   * Build a RenderOptions with the set configuration
   */
  def build(): FigletResult[RenderOptions] = {
    val defaultFontV = Figlet4s.loadFontInternal("standard")
    (options.font, defaultFontV).mapN { (fontO, defaultFont) =>
      val font = fontO.getOrElse(defaultFont)
      buildInner(options, font)
    }
  }

  private def buildInner(buildData: BuildData, font: FIGfont): RenderOptions =
    RenderOptions(
      font = font,
      layout = buildData.layout,
      maxWidth = buildData.maxWidth,
    )

  /** Use the default FIGfont */
  def defaultFont(): RenderOptionsBuilder =
    setFont(None.validNec)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): RenderOptionsBuilder = {
    val font = Figlet4s
      .loadFontInternal(fontName)
      .map(Some(_))
    setFont(font)
  }

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String): RenderOptionsBuilder = {
    val font = Figlet4s
      .loadFont(fontPath)
      .map(Some(_))
    setFont(font)
  }

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): RenderOptionsBuilder =
    setFont(Some(font).validNec)

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): RenderOptionsBuilder =
    setLayout(None)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): RenderOptionsBuilder =
    setLayout(Some(layout))

  /** Use the default Max Width */
  def defaultMaxWidth(): RenderOptionsBuilder =
    setMaxWidth(Int.MaxValue)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): RenderOptionsBuilder =
    setMaxWidth(maxWidth)

  //  Support  //

  private def setFont(font: FigletResult[Option[FIGfont]]) =
    new RenderOptionsBuilder(options.copy(font = font))

  private def setLayout(layout: Option[HorizontalLayout]) =
    new RenderOptionsBuilder(options.copy(layout = layout))

  private def setMaxWidth(maxWidth: Int) =
    new RenderOptionsBuilder(options.copy(maxWidth = maxWidth))
}

object RenderOptionsBuilder {
  final private case class BuildData(
      font: FigletResult[Option[FIGfont]] = None.validNec,
      layout: Option[HorizontalLayout] = None,
      maxWidth: Int = Int.MaxValue,
  )

  def apply(): RenderOptionsBuilder = new RenderOptionsBuilder(BuildData())
}
