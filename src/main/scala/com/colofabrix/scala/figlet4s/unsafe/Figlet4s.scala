package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.utils._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sClientAPI[Id] {

  /** The list of available internal fonts */
  def internalFonts: Vector[String] =
    InternalAPI.internalFonts[Id]

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FIGure =
    InternalAPI.renderString[Id](text, options)

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): FIGfont =
    InternalAPI
      .loadFontInternal[Id](name)
      .unsafeGet

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String = "ISO8859_1"): FIGfont =
    InternalAPI
      .loadFont[Id](path, encoding)
      .unsafeGet

  /** Returns a new options builder with default settings */
  def builder(): RenderOptionsBuilder =
    new RenderOptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): RenderOptionsBuilder =
    new RenderOptionsBuilder(text = text)

}
