package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.utils._
import com.colofabrix.scala.figlet4s.figfont._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sClientAPI[IO] {

  /** The list of available internal fonts */
  def internalFonts: IO[Vector[String]] =
    InternalAPI.internalFonts[IO]

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): IO[FIGure] =
    InternalAPI.renderString[IO](text, options)

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    InternalAPI
      .loadFontInternal[IO](name)
      .flatMap(_.asIO)

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    InternalAPI
      .loadFont[IO](path, encoding)
      .flatMap(_.asIO)

  /** Returns a new options builder with default settings */
  def builder(): RenderOptionsBuilder =
    new RenderOptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): RenderOptionsBuilder =
    new RenderOptionsBuilder(text = text)

}
