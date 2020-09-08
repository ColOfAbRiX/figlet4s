package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sAPI[Id] {

  /** The list of available internal fonts */
  @throws(classOf[FigletError])
  def internalFonts: Seq[String] =
    Figlet4sClient.internalFonts[Id]

  /** Loads one of the internal FIGfont */
  @throws(classOf[FigletError])
  def loadFontInternal(name: String = "standard"): FIGfont =
    Figlet4sClient
      .loadFontInternal[Id](name)
      .unsafeGet

  /** Loads a FIGfont from file */
  @throws(classOf[FigletError])
  def loadFont(path: String, encoding: String): FIGfont =
    Figlet4sClient
      .loadFont[Id](path, encoding)
      .unsafeGet

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient.renderString[Id](text, options)

  //  Builder  //

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

}
