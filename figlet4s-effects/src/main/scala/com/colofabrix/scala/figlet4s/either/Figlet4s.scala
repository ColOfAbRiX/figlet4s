package com.colofabrix.scala.figlet4s.either

import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import scala.io.Codec

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 *
 * This Figlet client returns results wrapped in Scala's Either
 */
object Figlet4s extends Figlet4sAPI[FigletEither] with Figlet4sEffectfulAPI[FigletEither] {

  /**
   * The list of available internal fonts
   *
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts: FigletEither[Seq[String]] =
    Figlet4sClient.internalFonts[FigletEither]

  /**
   * Loads one of the internal FIGfont
   *
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal(name: String = "standard"): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFontInternal[FigletEither](name)
      .flatMap(_.asEither)

  /**
   * Loads a FIGfont from file
   *
   * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
   * @param codec The codec of the file if textual. If it is a zipped file it will be ignored
   * @return The FIGfont loaded from the specified path
   */
  def loadFont(path: String, encoding: Codec = Codec.ISO8859): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFont[FigletEither](path, encoding)
      .flatMap(_.asEither)

  /**
   * Renders a given text as a FIGure
   *
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient
      .renderString[FigletEither](text, options)
      .unsafeGet

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /**
   * Renders a given text as a FIGure
   *
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderStringF(text: String, options: RenderOptions): FigletEither[FIGure] =
    Figlet4sClient.renderString[FigletEither](text, options)

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(): FigletEither[OptionsBuilder] =
    Right(builder())

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(text: String): FigletEither[OptionsBuilder] =
    Right(builder(text))

}
