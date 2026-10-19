package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import com.colofabrix.scala.figlet4s.options._

/** Catalogue of the known defects of the original figlet 2.2.5, used to skip the inputs it cannot render correctly */
object KnownFigletIssues {

  def isDisabled(options: TestRenderOptions): Boolean = {
    val fontDisabled =
      dodgyFonts.contains(options.fontName)

    val inputDisabled =
      disabledInputs.contains {
        (
          options.fontName,
          options.horizontalLayout.toString,
          options.printDirection.toString,
          options.justification.toString,
        )
      }

    val divergentDisabled =
      options.fontName == "c64/e__fist_" &&
      options.printDirection == PrintDirection.RightToLeft

    fontDisabled || inputDisabled || divergentDisabled
  }

  // Fonts for which the original figlet 2.2.5 cannot produce a usable render for any layout, direction and
  // justification combination
  private lazy val dodgyFonts: Set[String] =
    tsvLines("known-figlet-issues/broken-fonts.tsv").map(_.takeWhile(_ != '|')).toSet

  // Catalogue of the specific (font, layout, direction, justification) combinations for which the original figlet
  // 2.2.5 aborts or emits nondeterministic garbage
  private lazy val disabledInputs: Set[(String, String, String, String)] =
    tsvLines("known-figlet-issues/broken-inputs.tsv").map { line =>
      val fields = line.split('|')
      (fields(0), fields(1), fields(2), fields(3))
    }.toSet

  private def tsvLines(resource: String): Vector[String] = {
    val source = scala.io.Source.fromResource(resource)
    try {
      source
        .getLines()
        .filterNot(_.startsWith("#"))
        .filter(_.trim.nonEmpty)
        .toVector
    } finally {
      source.close()
    }
  }

}
