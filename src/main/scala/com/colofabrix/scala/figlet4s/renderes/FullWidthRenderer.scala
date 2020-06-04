package com.colofabrix.scala.figlet4s.renderes

import com.colofabrix.scala.figlet4s.figfont.FIGstring

final case class RenderOptions(
    maxWidth: Int = 80,
)

object FullWidthRenderer {

  def process(string: FIGstring): Vector[String] =
    for {
      line <- (0 until string.font.header.height).toVector
      char <- string.value
    } yield {
      string.font.apply(char).lines(line)
    }

}
