package com.colofabrix.scala.figlet4s.api

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * Common interface of all implementations of Figlet4s
 */
trait Figlet4sAPI[F[_]] {
  /** The list of available internal fonts */
  def internalFonts: F[Vector[String]]

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): F[FIGfont]

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String): F[FIGfont]

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): FIGure

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder
}

/**
 * Common interface forall implementations of Figlet4s that wrap results inside an effect
 */
trait Figlet4sEffectfulAPI[F[_]] {
  /** The list of available internal fonts */
  def internalFonts: F[Vector[String]]

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): F[FIGfont]

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String): F[FIGfont]

  /** Renders a given text as a FIGure */
  def renderStringF(text: String, options: RenderOptions): F[FIGure]

  /** Returns a new options builder with default settings */
  def builderF(): F[OptionsBuilder]

  /** Returns a new options builder with default settings and a set text */
  def builderF(text: String): F[OptionsBuilder]
}
