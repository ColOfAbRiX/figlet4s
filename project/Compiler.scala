// format: off

object Compiler {

  private lazy val cores = java.lang.Runtime.getRuntime.availableProcessors

  // Compiler options for Scala 2.13
  lazy val Options_2_13: Seq[String] = Seq(
    "-Ymacro-annotations",                       // Enable macro annotations
    "-Ybackend-parallelism", cores.toString,     // Enable paralellisation
    "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
    "-Ycache-macro-class-loader:last-modified",  // and macro definitions. This can lead to performance improvements.
    "-Ypatmat-exhaust-depth", "80",              // Limit the patmat exhaustiveness check depth
    "-Xnon-strict-patmat-analysis"               // Excessive strictness introduced with Scala 2.13.4 creates issues
  )

  // Options for the Splain plugin
  lazy val SplainOptions: Seq[String] = Seq(
    "-P:splain:all"
  )

  // Options for Java compiler
  lazy val JavacOptions: Seq[String] = Seq(
    "-Werror",
    "-source", "1.8",
    "-target", "1.8",
    "-g:lines",
  )
}

// format: on
