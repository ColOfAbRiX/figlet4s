// package com.colofabrix.scala.figlet4s

// import com.colofabrix.scala.figlet4s.options._
// import com.colofabrix.scala.figlet4s.testutils._
// import com.colofabrix.scala.figlet4s.unsafe._
// import org.scalatest.flatspec._
// import org.scalatest.matchers.should._

// class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

//   "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
//     val text = "j  k"

//     val builder =
//       Figlet4s
//         .builder(text)
//         .withInternalFont("cricket")
//         .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)

//     val computed = builder.render()
//     val expected = renderWithFiglet(builder.options, text)

//     computed should lookLike(expected)
//   }

// NOTE: More to test
//  - TestRenderOptions(6G8_?)x|4Puey#uUUl],bigchief,FontDefault,LeftToRight,FlushLeft)
//  - TestRenderOptions(fkBFdmq/(|*Q[Vg[/F#V.Q2cIqs#DF?8,)wBd,"7qC$Ft%INJAZ;Vb>LMr6,]uPd#,Mx,eftiwall,FontDefault,LeftToRight,FlushLeft)

// }
