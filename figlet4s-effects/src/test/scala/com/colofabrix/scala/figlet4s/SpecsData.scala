package com.colofabrix.scala.figlet4s

object SpecsData {

  final case class SpecsItem[A, B](input: A, expected: B)

  val sampleStandard: SpecsItem[String, String] = SpecsItem(
    input = "~ * Fao & C 123",
    expected = """  /\/|         _____              ___      ____   _ ____  _____ """ + "\n" +
      """ |/\/  __/\__ |  ___|_ _  ___    ( _ )    / ___| / |___ \|___ / """ + "\n" +
      """       \    / | |_ / _` |/ _ \   / _ \/\ | |     | | __) | |_ \ """ + "\n" +
      """       /_  _\ |  _| (_| | (_) | | (_>  < | |___  | |/ __/ ___) |""" + "\n" +
      """         \/   |_|  \__,_|\___/   \___/\/  \____| |_|_____|____/ """ + "\n" +
      """                                                                """,
  )

}
