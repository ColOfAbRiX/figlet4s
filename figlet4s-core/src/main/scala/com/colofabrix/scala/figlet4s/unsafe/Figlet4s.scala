package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation.
 *
 * This Figlet client returns only pure values and might throw exceptions
 */
object Figlet4s extends Figlet4sAPI[Id] {

  /**
   * The list of available internal fonts
   *
   * @return The collection of names of FIGfonts shipped with this library
   */
  @throws(classOf[FigletException])
  def internalFonts: Seq[String] =
    Figlet4sClient.internalFonts[Id]

  /**
   * Loads one of the internal FIGfont
   *
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  @throws(classOf[FigletException])
  def loadFontInternal(name: String = "standard"): FIGfont =
    Figlet4sClient
      .loadFontInternal[Id](name)
      .unsafeGet

  /**
   * Loads a FIGfont from file
   *
   * @param path     The path of the font file to load. It can be a .flf file or a zipped file.
   * @param encoding The encoding of the file if textual
   * @return The FIGfont loaded from the specified path
   */
  @throws(classOf[FigletException])
  def loadFont(path: String, encoding: String): FIGfont =
    Figlet4sClient
      .loadFont[Id](path, encoding)
      .unsafeGet

  /**
   * Renders a given text as a FIGure
   *
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient.renderString[Id](text, options)

  //  Builder  //

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

}
