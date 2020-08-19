package com.colofabrix.scala.figlet4s.either

import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sClientAPI[FigletEither] {

  /** The list of available internal fonts */
  def internalFonts: FigletEither[Vector[String]] =
    InternalAPI.internalFonts[FigletEither]

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FigletEither[FIGure] =
    InternalAPI.renderString[FigletEither](text, options)

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): FigletEither[FIGfont] =
    InternalAPI
      .loadFontInternal[FigletEither](name)
      .flatMap(_.asEither)

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String = "ISO8859_1"): FigletEither[FIGfont] =
    InternalAPI
      .loadFont[FigletEither](path, encoding)
      .flatMap(_.asEither)

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /** Returns a new options builder with default settings */
  def builderF(): FigletEither[OptionsBuilder] =
    Right(builder())

  /** Returns a new options builder with default settings and a set text */
  def builderF(text: String): FigletEither[OptionsBuilder] =
    Right(builder(text))

}
