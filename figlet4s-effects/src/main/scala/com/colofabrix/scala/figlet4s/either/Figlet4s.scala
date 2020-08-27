package com.colofabrix.scala.figlet4s.either

import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sAPI[FigletEither] with Figlet4sEffectfulAPI[FigletEither] {

  /** The list of available internal fonts */
  def internalFonts: FigletEither[List[String]] =
    Figlet4sClient.internalFonts[FigletEither]

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFontInternal[FigletEither](name)
      .flatMap(_.asEither)

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String = "ISO8859_1"): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFont[FigletEither](path, encoding)
      .flatMap(_.asEither)

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient
      .renderString[FigletEither](text, options)
      .unsafeGet

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /** Renders a given text as a FIGure */
  def renderStringF(text: String, options: RenderOptions): FigletEither[FIGure] =
    Figlet4sClient.renderString[FigletEither](text, options)

  /** Returns a new options builder with default settings */
  def builderF(): FigletEither[OptionsBuilder] =
    Right(builder())

  /** Returns a new options builder with default settings and a set text */
  def builderF(text: String): FigletEither[OptionsBuilder] =
    Right(builder(text))

}
