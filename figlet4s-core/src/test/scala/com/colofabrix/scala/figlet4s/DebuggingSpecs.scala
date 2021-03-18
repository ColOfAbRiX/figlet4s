// package com.colofabrix.scala.figlet4s

// import com.colofabrix.scala.figlet4s.options._
// import com.colofabrix.scala.figlet4s.testutils._
// import com.colofabrix.scala.figlet4s.unsafe._
// import org.scalatest.flatspec._
// import org.scalatest.matchers.should._

// class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

//   "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
//     Figlet4s.loadFontInternal("c64/a_zooloo")
//     Figlet4s.loadFontInternal("c64/star_war")

//     val text = "!\"£$ABCD1234abcd[]{}"

//     val builder =
//       Figlet4s
//         .builder(text)
//         .withMaxWidth(250)
//         .withInternalFont("c64/star_war")
//         .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)

//     println("COMPUTED:")
//     val computed = builder.render()
//     computed.foreachLine(line => println("|" + line + "|"))

//     println("EXPECTED:")
//     val expected = renderWithFiglet(builder.options, text)
//     expected.foreachLine(line => println("|" + line + "|"))

//     computed should lookLike(expected)
//   }

// }
