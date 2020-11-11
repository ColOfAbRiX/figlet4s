// package com.colofabrix.scala.figlet4s

// import com.colofabrix.scala.figlet4s.options._
// import com.colofabrix.scala.figlet4s.testutils._
// import com.colofabrix.scala.figlet4s.unsafe._
// import org.scalatest.flatspec._
// import org.scalatest.matchers.should._

// class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

//   "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
//     val text = ""

//     val builder =
//       Figlet4s
//         .builder(text)
//         .withInternalFont("whimsy")
//         .withHorizontalLayout(HorizontalLayout.HorizontalFitting)

//     val computed = builder.render()
//     val expected = renderWithFiglet(builder.options, text)

//     computed should lookLike(expected)
//     computed.print()
//     expected.print()
//   }

// }
