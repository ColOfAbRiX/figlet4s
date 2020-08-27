package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s extends Figlet4sAPI[IO] with Figlet4sEffectfulAPI[IO] {

  /** The list of available internal fonts */
  def internalFonts: IO[List[String]] =
    Figlet4sClient.internalFonts[IO]

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sClient
      .loadFontInternal[IO](name)
      .flatMap(_.asIO)

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    Figlet4sClient
      .loadFont[IO](path, encoding)
      .flatMap(_.asIO)

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient
      .renderString[IO](text, options)
      .unsafeRunSync()

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /** Returns a new options builder with default settings */
  def builderF(): IO[OptionsBuilder] =
    IO.pure(builder())

  /** Returns a new options builder with default settings and a set text */
  def builderF(text: String): IO[OptionsBuilder] =
    IO.pure(builder(text))

  /** Renders a given text as a FIGure */
  def renderStringF(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sClient.renderString[IO](text, options)

}
