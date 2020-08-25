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

/**
 * Common interface of all implementations of option builders
 */
trait OptionsBuilderAPI[F[_]] {
  /** The text to render */
  def text: String

  /** Renders the text into a FIGure */
  def render(): FIGure

  /** Renders a given text into a FIGure */
  def render(text: String): FIGure

  /** Returns the render options */
  def options: F[RenderOptions]
}

/**
 * Common interface of all implementations of option builders that wrap results inside an effect
 */
trait OptionsBuilderEffectfulAPI[F[_]] {
  /** The text to render */
  def text: String

  /** The text to render */
  def textF: F[String]

  /** Renders the text into a FIGure */
  def render(): FIGure

  /** Renders the text into a FIGure */
  def renderF(): F[FIGure]

  /** Renders a given text into a FIGure */
  def render(text: String): FIGure

  /** Renders a given text into a FIGure */
  def renderF(text: String): F[FIGure]
}

/**
 * Common interface of all implementations of option builders
 */
trait FIGureAPI[F[_]] {
  /** Apply a function to each line of the FIGure */
  def foreachLine[A](f: String => A): F[Unit]

  /** Print the FIGure */
  def print(): F[Unit]

  /** The figure as a collection of String, one String per displayed line */
  def asVector(): Vector[String]

  /** The figure as single String and newline characters */
  def asString(): String
}

/**
 * Common interface of all implementations of option builders that wrap results inside an effect
 */
trait FIGureEffectfulAPI[F[_]] {
  /** Apply a function to each line of the FIGure */
  def foreachLine[A](f: String => A): F[Unit]

  /** Print the FIGure */
  def print(): F[Unit]

  /** The figure as a collection of String, one String per displayed line */
  def asVectorF(): F[Vector[String]]

  /** The figure as single String and newline characters */
  def asStringF(): F[String]
}
