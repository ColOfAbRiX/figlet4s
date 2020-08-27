package com.colofabrix.scala.figlet4s.api

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * Common interface of all implementations of Figlet4s
 *
 * @tparam F The effect that wraps the results of the functions
 */
trait Figlet4sAPI[F[_]] {
  /**
   * The list of available internal fonts
   *
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts: F[List[String]]

  /**
   * Loads one of the internal FIGfont
   *
   * @param name The name of the internal font to load
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal(name: String = "standard"): F[FIGfont]

  /**
   * Loads a FIGfont from file
   *
   * @param path The path of the font file to load. It can be a .flf file or a zipped file.
   * @param encoding The encoding of the file if textual
   * @return The FIGfont loaded from the specified path
   */
  def loadFont(path: String, encoding: String): F[FIGfont]

  /**
   * Renders a given text as a FIGure
   *
   * @param text The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString(text: String, options: RenderOptions): FIGure

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builder(): OptionsBuilder

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builder(text: String): OptionsBuilder
}

/**
 * Common interface forall implementations of Figlet4s that wrap all results of functions inside an effect
 *
 * @tparam F The effect that wraps the results of the functions
 */
trait Figlet4sEffectfulAPI[F[_]] {
  /**
   * The list of available internal fonts
   *
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts: F[List[String]]

  /**
   * Loads one of the internal FIGfont
   *
   * @param name The name of the internal font to load
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal(name: String = "standard"): F[FIGfont]

  /**
   * Loads a FIGfont from file
   *
   * @param path The path of the font file to load. It can be a .flf file or a zipped file.
   * @param encoding The encoding of the file if textual
   * @return The FIGfont loaded from the specified path
   */
  def loadFont(path: String, encoding: String): F[FIGfont]

  /**
   * Renders a given text as a FIGure
   *
   * @param text The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderStringF(text: String, options: RenderOptions): F[FIGure]

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(): F[OptionsBuilder]

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(text: String): F[OptionsBuilder]
}
