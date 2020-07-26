package com.colofabrix.scala.figlet4s

import cats._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.utils._

package object unsafe {

  implicit class RenderOptionsBuilderOps(val self: RenderOptionsBuilder) extends OptionsBuilderClientAPI[Id] {
    /** The text to render */
    def text: Id[String] = self.text

    /** Renders a given text as a FIGure and throws exceptions in case of errors */
    def render(): FIGure =
      Figlet4s.renderString(self.text, options)

    /** Builds an instance of RenderOptions */
    def options: RenderOptions = {
      val font = self
        .font
        .unsafeGet
        .getOrElse(Figlet4s.loadFontInternal())

      RenderOptions(font, self.layout, self.maxWidth)
    }
  }

  implicit class FIGureOps(val figure: FIGure) extends FIGureClientAPI[Id] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): Unit =
      figure.cleanLines.foreach(_.foreach(f))

    /** Print the FIGure */
    def print(): Unit =
      figure.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): Vector[String] =
      figure.lines.map(_.value.mkString("\n"))

    /** The figure as single String */
    def asString(): String =
      asVector().mkString("\n")
  }

}
