package dependencies

/**
 * Project dependencies
 */
object Dependencies extends Libraries with CompilerPlugins {

  lazy val ScalaLangVersion = "2.13.1"

  //  DEPENDENCY BUNDLES  //

  lazy val CatsBundle       = Seq(CatsCoreDep, CatsEffectDep, ScalaTestCatsDep)
  lazy val EnumeratumBundle = Seq(EnumeratumDep, EnumeratumCatsDep)
  lazy val TestingBundle    = Seq(ScalaTestDep, ScalaCheckDep, ScalaTestCatsDep)

}
