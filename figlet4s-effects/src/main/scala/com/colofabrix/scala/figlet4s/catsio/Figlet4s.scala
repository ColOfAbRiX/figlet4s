package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.core._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import scala.io.Codec

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 *
 * This Figlet client returns results wrapped in cats' IO
 */
object Figlet4s extends Figlet4sAPI[IO] with Figlet4sEffectfulAPI[IO] {

  /**
   * The list of available internal fonts
   *
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts: IO[Seq[String]] =
    Figlet4sClient.internalFonts[IO]

  /**
   * Loads one of the internal FIGfont
   *
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sClient
      .loadFontInternal[IO](name)
      .flatMap(_.asIO)

  /**
   * Loads a FIGfont from file
   *
   * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
   * @param codec The codec of the file if textual. If it is a zipped file it will be ignored
   * @return The FIGfont loaded from the specified path
   */
  def loadFont(path: String, codec: Codec = Codec.ISO8859): IO[FIGfont] =
    Figlet4sClient
      .loadFont[IO](path, codec)
      .flatMap(_.asIO)

  /**
   * Renders a given text as a FIGure
   *
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString(text: String, options: RenderOptions): FIGure =
    renderStringF(text, options).unsafeRunSync()

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builder(): OptionsBuilder =
    builderF().unsafeRunSync()

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builder(text: String): OptionsBuilder =
    builderF(text).unsafeRunSync()

  /**
   * Renders a given text as a FIGure
   *
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderStringF(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sClient.renderString[IO](text, options)

  /**
   * Returns a new options builder with default settings
   *
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(): IO[OptionsBuilder] =
    IO.pure(OptionsBuilder())

  /**
   * Returns a new options builder with default settings and containing the specified text to render
   *
   * @param text The text to render
   * @return An OptionBuilder to build the rendering options
   */
  def builderF(text: String): IO[OptionsBuilder] =
    IO.pure(OptionsBuilder(BuilderAction.SetTextAction(text)))

}
