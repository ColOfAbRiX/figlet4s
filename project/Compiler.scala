// format: off

/**
 * Compiler settings
 *
 * Note: Scala compiler options are managed by sbt-tpolecat plugin,
 * which provides sensible defaults for all Scala versions.
 */
object Compiler {

  // Options for Java compiler
  lazy val JavacOptions: Seq[String] = Seq(
    "-Werror",
    "-source", "11",
    "-target", "11",
    "-g:lines",
  )
}

// format: on
