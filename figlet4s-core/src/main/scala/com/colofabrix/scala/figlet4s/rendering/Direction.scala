package com.colofabrix.scala.figlet4s.rendering

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.{ PrintDirection => ClientPrintDirection, RenderOptions }

object Direction {

  def isPrintLeft(options: RenderOptions): Boolean =
    options.printDirection match {
      case ClientPrintDirection.LeftToRight => true
      case ClientPrintDirection.RightToLeft => false
      case ClientPrintDirection.FontDefault =>
        options.font.settings.printDirection match {
          case FIGfontParameters.PrintDirection.LeftToRight => true
          case FIGfontParameters.PrintDirection.RightToLeft => false
        }
    }

}
