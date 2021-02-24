package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.core.Figlet4sClient
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class OptionsBuilderSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers {

  //  Text  //

  "text" should "set the text to render" in {
    val expected = "Hello, World!"
    val computed = Figlet4s.builder().text(expected).render().value
    computed should equal(expected)
  }

  it should "use the last set text" in {
    val expected = "William Shakespeare"
    val computed = Figlet4s
      .builder("All the world’s a stage, and all the men and women merely players.")
      .text("They have their exits and their entrances; And one man in his time plays many parts.")
      .text(expected)
      .render()
      .value
    computed should equal(expected)
  }

  //  Fonts  //

  "defaultFont/withInternalFont" should "set and load the default font in the RenderingOptions" in {
    val computed = Figlet4s.builder().defaultFont().options.font.name
    val expected = Figlet4sClient.defaultFont
    computed should equal(expected)
  }

  it should "set and load an internal font in the RenderingOptions" in {
    val expected = "alligator"
    val computed = Figlet4s.builder().withInternalFont(expected).options.font.name
    computed should equal(expected)
  }

  it should "use the last font set" in {
    val expected = "alligator"
    val computed = Figlet4s
      .builder()
      .withInternalFont("shadow")
      .defaultFont()
      .withInternalFont(expected)
      .options
      .font
      .name
    computed should equal(expected)
  }

  it should "set and load a font from file" in {
    val expected = "alligator"
    val cwd      = System.getProperty("user.dir")
    val font     = s"$cwd/figlet4s-core/src/main/resources/fonts/$expected.flf"
    val computed = Figlet4s.builder().withFont(font).options.font.name
    computed should equal(expected)
  }

  //  Horizontal layout  //

  "defaultHorizontalLayout/withHorizontalLayout" should "set the horizontal layout to font default" in {
    val computed = Figlet4s.builder().defaultHorizontalLayout().options.horizontalLayout
    val expected = HorizontalLayout.FontDefault
    computed should equal(expected)
  }

  it should "set an horizontal layout" in {
    val expected = HorizontalLayout.HorizontalFitting
    val computed = Figlet4s.builder().withHorizontalLayout(expected).options.horizontalLayout
    computed should equal(expected)
  }

  it should "use the last set horizontal layout" in {
    val expected = HorizontalLayout.HorizontalFitting
    val computed = Figlet4s
      .builder()
      .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)
      .defaultHorizontalLayout()
      .withHorizontalLayout(expected)
      .options
      .horizontalLayout
    computed should equal(expected)
  }

  //  Max Width  //

  "defaultMaxWidth/withMaxWidth" should "set the MaxWidth to the default value" in {
    val computed = Figlet4s.builder().defaultMaxWidth().options.maxWidth
    val expected = Figlet4sClient.defaultMaxWidth
    computed should equal(expected)
  }

  it should "set a MaxWidth" in {
    val expected = 42
    val computed = Figlet4s.builder().withMaxWidth(expected).options.maxWidth
    computed should equal(expected)
  }

  it should "use the last set MaxWidth" in {
    val expected = 42
    val computed = Figlet4s
      .builder()
      .withMaxWidth(120)
      .defaultMaxWidth()
      .withMaxWidth(expected)
      .options
      .maxWidth
    computed should equal(expected)
  }

  //  Justification  //

  "defaultJustification/withJustification" should "set the justification to font default" in {
    val computed = Figlet4s.builder().defaultJustification().options.justification
    val expected = Justification.FontDefault
    computed should equal(expected)
  }

  it should "set a justification" in {
    val expected = Justification.Center
    val computed = Figlet4s.builder().withJustification(expected).options.justification
    computed should equal(expected)
  }

  it should "use the last set justification" in {
    val expected = Justification.Center
    val computed = Figlet4s
      .builder()
      .withJustification(Justification.FlushRight)
      .defaultJustification()
      .withJustification(expected)
      .options
      .justification
    computed should equal(expected)
  }

  //  Print Direction  //

  "defaultPrintDirection/withPrintDirection" should "set the PrintDirection to font default" in {
    val computed = Figlet4s.builder().defaultPrintDirection().options.printDirection
    val expected = PrintDirection.FontDefault
    computed should equal(expected)
  }

  it should "set a PrintDirection" in {
    val expected = PrintDirection.RightToLeft
    val computed = Figlet4s.builder().withPrintDirection(expected).options.printDirection
    computed should equal(expected)
  }

  it should "use the last set PrintDirection" in {
    val expected = PrintDirection.RightToLeft
    val computed = Figlet4s
      .builder()
      .withPrintDirection(PrintDirection.LeftToRight)
      .defaultPrintDirection()
      .withPrintDirection(expected)
      .options
      .printDirection
    computed should equal(expected)
  }

}
