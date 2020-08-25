package com.colofabrix.scala.figlet4s.api

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * Common interface of all implementations of option builders
 */
trait OptionsBuilderAPI[F[_]] {
  /** Builds and returns the render options */
  def options: F[RenderOptions]

  /** The text to render */
  def text: F[String]

  /** Builds the options and then renders the text into a FIGure */
  def render(): F[FIGure]

  /** Builds the options and then renders a given text into a FIGure */
  def render(text: String): F[FIGure]
}
