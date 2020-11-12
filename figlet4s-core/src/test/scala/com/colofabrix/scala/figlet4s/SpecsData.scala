package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._

object SpecsData {

  val standardBuilder: OptionsBuilder = Figlet4s.builder().withInternalFont(Figlet4sClient.defaultFont)
  val standardInput: String           = "~ * Fao & C 123"
  val standardLines: SubLines = SubLines(
    Vector(
      """ /\/|         _____              ___      ____   _ ____  _____ """,
      """|/\/  __/\__ |  ___|_ _  ___    ( _ )    / ___| / |___ \|___ / """,
      """      \    / | |_ / _` |/ _ \   / _ \/\ | |     | | __) | |_ \ """,
      """      /_  _\ |  _| (_| | (_) | | (_>  < | |___  | |/ __/ ___) |""",
      """        \/   |_|  \__,_|\___/   \___/\/  \____| |_|_____|____/ """,
      """                                                               """,
    ),
  )

}
