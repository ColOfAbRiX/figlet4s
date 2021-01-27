---
layout: docs
title: Getting started
permalink: docs/getting-started
---
# Getting started

These examples show step-by-step how to use Figlet4s. This is the basic scenario where we assume
users don't want to use or don't want to deal with effects, and we want errors to be thrown as
exceptions.

The general way to use Figlet4s involves 3 steps:

* first obtain a builder to set the options;
* then configure the options of the builder, if needed;
* last render a text into a FIGure

Once you have a FIGure you can do further processing like printing it or converting it to a string.

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object QuickStartMain extends App {

  // 1. Obtain an options builder
  val builder = Figlet4s.builder()

  // 2. In this example we use the default configuration

  // 3. Render a text into a FIGure
  val figure = builder.render("Hello, World!")

  // Do something with the FIGure
  figure.print()

}
```

## Setting options

In this example we see some options that you can configure, and we see a more compact way of making
the calls, without storing objects at each step.

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object ShowcaseOptionsMain extends App {

  Figlet4s
    .builder("Hello, World!")      // 1. Create the options builder with a text to render
    .withMaxWidth(80)              // 2. Max width of the text
    .withInternalFont("calgphy2")  // 2. Set the font
    .defaultMaxWidth()             // 2. Go back to the default max  width
    .withHorizontalLayout(
      HorizontalLayout.FullWidth   // 2. Choose a layout
    )
    .text("Hello, Scala!")         // 2. Change the text to render
    .render()                      // 3. Render the text to a FIGure
    .print()                       // Do something with the FIGure

}
```

## Playing with the FIGure

When the FIGure has been rendered you can play with it. We've seen how to print it to screen, but
you  can also obtain the displayable lines (SubLines) that compose the FIGure as a collection and
then you can manipulate it.

The logo of Figlet4s has been created with this code:

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object UsingFIGureMain extends App {

  Figlet4s
    .builder("Figlet4s")
    .render()
    .asSeq()
    .zipWithIndex
    .foreach { case (line, i) =>
      if (i == 4) println(s"$line  Figlet, but in Scala")
      else println(line)
    }

}
```
