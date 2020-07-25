package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.utils._
import com.colofabrix.scala.figlet4s.figfont._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s {
  def internalFonts: IO[Vector[String]] =
    Figlet4sAPI.internalFonts[IO]

  def renderString(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sAPI.renderString[IO](text, options)

  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sAPI
      .loadFontInternal[IO](name)
      .flatMap(_.asIO)

  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    Figlet4sAPI
      .loadFont[IO](path, encoding)
      .flatMap(_.asIO)
}
