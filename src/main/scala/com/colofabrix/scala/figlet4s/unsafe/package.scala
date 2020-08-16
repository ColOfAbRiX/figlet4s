package com.colofabrix.scala.figlet4s

import cats._
import cats.implicits._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.utils._

package object unsafe {

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderClientAPI[Id] {
    private val buildOptions = self.compile[Id]

    /** The text to render */
    def text: String = buildOptions.text

    /** Renders a given text as a FIGure and throws exceptions in case of errors */
    def render(): FIGure =
      Figlet4s.renderString(buildOptions.text, options)

    /** Renders a given text into a FIGure */
    def render(text: String): Id[FIGure] =
      self
        .text(text)
        .render()

    /** Builds an instance of RenderOptions */
    def options: RenderOptions = {
      val font =
        buildOptions
          .font
          .getOrElse(Figlet4s.loadFontInternal().validNec)
          .unsafeGet

      val horizontalLayout = HorizontalLayout.toInternalLayout(
        buildOptions.horizontalLayout,
        font
      )

      val maxWidth =
        buildOptions
          .maxWidth
          .getOrElse(Int.MaxValue)

      val printDirection =
        buildOptions
          .printDirection
          .getOrElse(PrintDirection.LeftToRight)

      RenderOptions(font, maxWidth, horizontalLayout, printDirection)
    }
  }

  implicit class FIGureOps(val self: FIGure) extends FIGureClientAPI[Id] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /** Print the FIGure */
    def print(): Unit =
      self.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): Vector[String] =
      self.lines.map(_.value.mkString("\n"))

    /** The figure as single String */
    def asString(): String =
      asVector().mkString("\n")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] class FigletResultOps[E, A](val self: FigletResult[A]) extends AnyVal {
    /** Unsafely returns the value inside the Validated or throws an exception with the first error */
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

}
