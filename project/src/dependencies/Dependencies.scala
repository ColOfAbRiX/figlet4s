package dependencies

/**
 * Project dependencies
 */
object Dependencies extends Libraries with CompilerPlugins {

  lazy val ScalaLangVersion = "2.13.2"

  //  DEPENDENCY BUNDLES  //

  lazy val CatsBundle = Seq(CatsCoreDep, CatsCoreDep, CatsEffectDep)

}
