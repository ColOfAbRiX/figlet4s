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

  /** @inheritdoc */
  @throws(classOf[FigletError])
  def internalFonts: Seq[String] =
    Figlet4sClient.internalFonts[Id]

  /** @inheritdoc */
  @throws(classOf[FigletError])
  def loadFontInternal(name: String = "standard"): FIGfont =
    Figlet4sClient
      .loadFontInternal[Id](name)
      .unsafeGet

  /** @inheritdoc */
  @throws(classOf[FigletError])
  def loadFont(path: String, encoding: String): FIGfont =
    Figlet4sClient
      .loadFont[Id](path, encoding)
      .unsafeGet

  /** @inheritdoc */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient.renderString[Id](text, options)

  //  Builder  //

  /** @inheritdoc */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** @inheritdoc */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

}
