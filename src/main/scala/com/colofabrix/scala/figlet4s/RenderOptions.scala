package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.Figlet4s
import RenderOptionsBuilder._
import cats.instances.string

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
class RenderOptionsBuilder private (private val options: Figlet4sData) {
  /** Renders a given text as a FIGure */
  def render(): FigletResult[FIGure] = {
    val defaultFontV = Figlet4s.loadFontInternal("standard")
    (options.font, defaultFontV).mapN { (fontO, defaultFont) =>
      val font = fontO.getOrElse(defaultFont)
      Figlet4s.renderString(options.text, buildRenderOptions(options, font))
    }
  }

  /** Renders a given text as a FIGure throwing an exception for any issue */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def unsafeRender(): FIGure =
    render().fold(e => throw e.head, identity)

  /** Use the default FIGfont */
  def defaultFont(): RenderOptionsBuilder =
    setFont(None.validNec)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): RenderOptionsBuilder = {
    val font = Figlet4s.loadFontInternal(fontName).map(Some(_))
    setFont(font)
  }

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String): RenderOptionsBuilder = {
    val font = Figlet4s.loadFont(fontPath).map(Some(_))
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

  /** Use the specified Max Width */
  def text(text: String): RenderOptionsBuilder =
    setText(text)

  //  Support  //

  private def buildRenderOptions(buildData: Figlet4sData, font: FIGfont): RenderOptions =
    RenderOptions(
      font = font,
      layout = buildData.layout,
      maxWidth = buildData.maxWidth,
    )

  private def setFont(font: FigletResult[Option[FIGfont]]) =
    new RenderOptionsBuilder(options.copy(font = font))

  private def setLayout(layout: Option[HorizontalLayout]) =
    new RenderOptionsBuilder(options.copy(layout = layout))

  private def setMaxWidth(maxWidth: Int) =
    new RenderOptionsBuilder(options.copy(maxWidth = maxWidth))

  private def setText(text: String) =
    new RenderOptionsBuilder(options.copy(text = text))
}

object RenderOptionsBuilder {
  final private case class Figlet4sData(
      text: String = "",
      font: FigletResult[Option[FIGfont]] = None.validNec,
      layout: Option[HorizontalLayout] = None,
      maxWidth: Int = Int.MaxValue,
  )

  def apply(): RenderOptionsBuilder =
    new RenderOptionsBuilder(Figlet4sData())

  def apply(text: String): RenderOptionsBuilder =
    new RenderOptionsBuilder(Figlet4sData(text = text))
}
