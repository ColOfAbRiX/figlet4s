package com.colofabrix.java.figlet4s

import com.colofabrix.java.figlet4s.options._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class JavaOptionsBuilderSpecs extends AnyFlatSpec with Matchers {

  //  Text  //

  "text" should "set the text to render" in {
    val expected = "Hello, World!"
    val computed = Figlet4s.builder().text(expected).render().getValue
    computed should equal(expected)
  }

  it should "use the last set text" in {
    val expected = "William Shakespeare"
    val computed = Figlet4s
      .builder("All the world’s a stage, and all the men and women merely players.")
      .text("They have their exits and their entrances; And one man in his time plays many parts.")
      .text(expected)
      .render()
      .getValue
    computed should equal(expected)
  }

  //  Fonts  //

  "defaultFont/withInternalFont" should "set and load the default font in the RenderingOptions" in {
    val computed = Figlet4s.builder().defaultFont().getOptions.getFont.name
    val expected = "standard"
    computed should equal(expected)
  }

  it should "set and load an internal font in the RenderingOptions" in {
    val expected = "alligator"
    val computed = Figlet4s.builder().withInternalFont(expected).getOptions.getFont.name
    computed should equal(expected)
  }

  it should "use the last font set" in {
    val expected = "alligator"
    val computed = Figlet4s
      .builder()
      .withInternalFont("shadow")
      .defaultFont()
      .withInternalFont(expected)
      .getOptions
      .getFont
      .name
    computed should equal(expected)
  }

  it should "set and load a font from file" in {
    val expected = "alligator"
    val cwd      = System.getProperty("user.dir")
    val font     = s"$cwd/figlet4s-core/src/main/resources/fonts/$expected.flf"
    val computed = Figlet4s.builder().withFont(font).getOptions.getFont.name
    computed should equal(expected)
  }

  //  Horizontal layout  //

  "defaultHorizontalLayout/withHorizontalLayout" should "set the horizontal layout to font default" in {
    val computed = Figlet4s.builder().defaultHorizontalLayout().getOptions.getHorizontalLayout
    val expected = HorizontalLayout.FONT_DEFAULT
    computed should equal(expected)
  }

  it should "set an horizontal layout" in {
    val expected = HorizontalLayout.HORIZONTAL_FITTING
    val computed = Figlet4s.builder().withHorizontalLayout(expected).getOptions.getHorizontalLayout
    computed should equal(expected)
  }

  it should "use the last set horizontal layout" in {
    val expected = HorizontalLayout.HORIZONTAL_FITTING
    val computed = Figlet4s
      .builder()
      .withHorizontalLayout(HorizontalLayout.HORIZONTAL_SMUSHING)
      .defaultHorizontalLayout()
      .withHorizontalLayout(expected)
      .getOptions
      .getHorizontalLayout
    computed should equal(expected)
  }

  //  Max Width  //

  "defaultMaxWidth/withMaxWidth" should "set the MaxWidth to the default value" in {
    val computed = Figlet4s.builder().defaultMaxWidth().getOptions.getMaxWidth
    val expected = 80
    computed should equal(expected)
  }

  it should "set a MaxWidth" in {
    val expected = 42
    val computed = Figlet4s.builder().withMaxWidth(expected).getOptions.getMaxWidth
    computed should equal(expected)
  }

  it should "use the last set MaxWidth" in {
    val expected = 42
    val computed = Figlet4s
      .builder()
      .withMaxWidth(120)
      .defaultMaxWidth()
      .withMaxWidth(expected)
      .getOptions
      .getMaxWidth
    computed should equal(expected)
  }

  //  Justification  //

  "defaultJustification/withJustification" should "set the justification to font default" in {
    val computed = Figlet4s.builder().defaultJustification().getOptions.getJustification
    val expected = Justification.FONT_DEFAULT
    computed should equal(expected)
  }

  it should "set a justification" in {
    val expected = Justification.CENTER
    val computed = Figlet4s.builder().withJustification(expected).getOptions.getJustification
    computed should equal(expected)
  }

  it should "use the last set justification" in {
    val expected = Justification.CENTER
    val computed = Figlet4s
      .builder()
      .withJustification(Justification.FLUSH_RIGHT)
      .defaultJustification()
      .withJustification(expected)
      .getOptions
      .getJustification
    computed should equal(expected)
  }

  //  Print Direction  //

  "defaultPrintDirection/withPrintDirection" should "set the PrintDirection to font default" in {
    val computed = Figlet4s.builder().defaultPrintDirection().getOptions.getPrintDirection
    val expected = PrintDirection.FONT_DEFAULT
    computed should equal(expected)
  }

  it should "set a PrintDirection" in {
    val expected = PrintDirection.RIGHT_TO_LEFT
    val computed = Figlet4s.builder().withPrintDirection(expected).getOptions.getPrintDirection
    computed should equal(expected)
  }

  it should "use the last set PrintDirection" in {
    val expected = PrintDirection.RIGHT_TO_LEFT
    val computed = Figlet4s
      .builder()
      .withPrintDirection(PrintDirection.LEFT_TO_RIGHT)
      .defaultPrintDirection()
      .withPrintDirection(expected)
      .getOptions
      .getPrintDirection
    computed should equal(expected)
  }

}
