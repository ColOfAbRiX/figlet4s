package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
object Figlet4s {
  def internalFonts: IO[Vector[String]] =
    Figlet4sAPI.internalFonts[IO]

  def renderString(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sAPI.renderString[IO](text, options)

  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sAPI
      .loadFontInternal[IO](name)
      .flatMap(errorsAsIo)

  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    Figlet4sAPI
      .loadFont[IO](path, encoding)
      .flatMap(errorsAsIo)

  private def errorsAsIo[A](x: FigletResult[A]): IO[A] =
    x.fold(e => IO.raiseError(e.head), IO(_))
}
