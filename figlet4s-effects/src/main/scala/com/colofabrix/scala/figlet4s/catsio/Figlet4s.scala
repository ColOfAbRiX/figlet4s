package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 *
 * This Figlet client returns results wrapped in cats' IO
 */
object Figlet4s extends Figlet4sAPI[IO] with Figlet4sEffectfulAPI[IO] {

  /** @inheritdoc */
  def internalFonts: IO[Seq[String]] =
    Figlet4sClient.internalFonts[IO]

  /** @inheritdoc */
  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sClient
      .loadFontInternal[IO](name)
      .flatMap(_.asIO)

  /** @inheritdoc */
  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    Figlet4sClient
      .loadFont[IO](path, encoding)
      .flatMap(_.asIO)

  /** @inheritdoc */
  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sClient
      .renderString[IO](text, options)
      .unsafeRunSync()

  /** @inheritdoc */
  def builder(): OptionsBuilder =
    new OptionsBuilder()

  /** @inheritdoc */
  def builder(text: String): OptionsBuilder =
    new OptionsBuilder(BuilderAction.SetTextAction(text) :: Nil)

  /** @inheritdoc */
  def builderF(): IO[OptionsBuilder] =
    IO.pure(builder())

  /** @inheritdoc */
  def builderF(text: String): IO[OptionsBuilder] =
    IO.pure(builder(text))

  /** @inheritdoc */
  def renderStringF(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sClient.renderString[IO](text, options)

}
