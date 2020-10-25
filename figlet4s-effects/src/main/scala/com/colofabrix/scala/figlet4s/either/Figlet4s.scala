package com.colofabrix.scala.figlet4s.either

import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 *
 * This Figlet client returns results wrapped in Scala's Either
 */
object Figlet4s extends Figlet4sAPI[FigletEither] with Figlet4sEffectfulAPI[FigletEither] {

  /** @inheritdoc */
  def internalFonts: FigletEither[Seq[String]] =
    Figlet4sClient.internalFonts[FigletEither]

  /** @inheritdoc */
  def loadFontInternal(name: String = "standard"): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFontInternal[FigletEither](name)
      .flatMap(_.asEither)

  /** @inheritdoc */
  def loadFont(path: String, encoding: String = "ISO8859_1"): FigletEither[FIGfont] =
    Figlet4sClient
      .loadFont[FigletEither](path, encoding)
      .flatMap(_.asEither)

  /** @inheritdoc */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient
      .renderString[FigletEither](text, options)
      .unsafeGet

  /** @inheritdoc */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** @inheritdoc */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /** @inheritdoc */
  def renderStringF(text: String, options: RenderOptions): FigletEither[FIGure] =
    Figlet4sClient.renderString[FigletEither](text, options)

  /** @inheritdoc */
  def builderF(): FigletEither[OptionsBuilder] =
    Right(builder())

  /** @inheritdoc */
  def builderF(text: String): FigletEither[OptionsBuilder] =
    Right(builder(text))

}
