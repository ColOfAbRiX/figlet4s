package com.colofabrix.java.figlet4s

import com.colofabrix.scala.figlet4s.StandardTestData
import scala.jdk.CollectionConverters

object JavaStandardTestData {

  val standardBuilder: OptionsBuilder = new OptionsBuilder(StandardTestData.standardBuilder)

  val standardInput: String = StandardTestData.standardInput

  val standardLines: java.util.List[String] =
    CollectionConverters.SeqHasAsJava(StandardTestData.standardLines.value).asJava

}
