package com.colofabrix.scala.figlet4s.api

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.rendering.RenderOptions

/**
 * Common interface of all implementations of Figlet4s
 */
trait Figlet4sClientAPI[F[_]] {
  /** The list of available internal fonts */
  def internalFonts: F[Vector[String]]

  /** Renders a given text as a FIGure */
  def renderString(text: String, options: RenderOptions): F[FIGure]

  /** Loads one of the internal FIGfont */
  def loadFontInternal(name: String = "standard"): F[FIGfont]

  /** Loads a FIGfont from file */
  def loadFont(path: String, encoding: String): F[FIGfont]

  /** Returns a new options builder with default settings */
  def builder(): OptionsBuilder

  /** Returns a new options builder with default settings and a set text */
  def builder(text: String): OptionsBuilder

  /** Returns a new options builder with default settings */
  def builderF(): F[OptionsBuilder]

  /** Returns a new options builder with default settings and a set text */
  def builderF(text: String): F[OptionsBuilder]
}

/**
 * Common interface of all implementations of option builders
 */
trait OptionsBuilderClientAPI[F[_]] {
  /** The text to render */
  def text: F[String]

  /** Renders the text into a FIGure */
  def render(): F[FIGure]

  /** Returns the render options */
  def options: F[RenderOptions]
}

/**
 * Common interface of all implementations of option builders
 */
trait FIGureClientAPI[F[_]] {
  /** Apply a function to each line of the FIGure */
  def foreachLine[A](f: String => A): F[Unit]

  /** Print the FIGure */
  def print(): F[Unit]

  /** The figure as a Vector of String */
  def asVector(): F[Vector[String]]

  /** The figure as single String */
  def asString(): F[String]
}
