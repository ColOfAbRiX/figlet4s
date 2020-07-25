package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.utils._
import com.colofabrix.scala.figlet4s.figfont._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s {

  def internalFonts: Vector[String] =
    Figlet4sAPI.internalFonts[Id]

  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sAPI.renderString[Id](text, options)

  def loadFontInternal(name: String = "standard"): FIGfont =
    Figlet4sAPI
      .loadFontInternal[Id](name)
      .unsafeGet

  def loadFont(path: String, encoding: String = "ISO8859_1"): FIGfont =
    Figlet4sAPI
      .loadFont[Id](path, encoding)
      .unsafeGet

}
