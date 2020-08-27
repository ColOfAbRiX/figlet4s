package com.colofabrix.scala.figlet4s.api

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

/**
 * Common interface of all implementations of option builders
 *
 * @tparam F The effect that wraps the results of the functions
 */
trait OptionsBuilderAPI[F[_]] {
  /**
   * Builds and returns the render options
   *
   * @return The RenderOptions resulting from building the internal state
   */
  def options: F[RenderOptions]

  /**
   * The text to render
   *
   * @return The text to render as String
   */
  def text: F[String]

  /**
   * Builds the options and then renders the text into a FIGure
   *
   * @return A FIGure representing the rendered text
   */
  def render(): F[FIGure]

  /**
   * Builds the options and then renders the text into a FIGure
   *
   * @param text The text to render
   * @return A FIGure representing the rendered text
   */
  def render(text: String): F[FIGure]
}
