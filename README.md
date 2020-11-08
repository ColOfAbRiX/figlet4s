```
 _____ _       _      _   _  _
|  ___(_) __ _| | ___| |_| || |  ___
| |_  | |/ _` | |/ _ \ __| || |_/ __|
|  _| | | (_| | |  __/ |_|__   _\__ \
|_|   |_|\__, |_|\___|\__|  |_| |___/  Figlet, but in Scala
         |___/
```

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Actions Status](https://github.com/ColOfAbRiX/figlet4s/workflows/tests/badge.svg)](https://github.com/ColOfAbRiX/figlet4s/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.colofabrix.scala/figlet4s/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.colofabrix.scala/figlet4s)
[![Gitter chat](https://badges.gitter.im/ColOfAbRiX/figlet4s.png)](https://gitter.im/figlet4s/community)

# Figlet4s

This is a library implementation of [FIGlet](http://www.figlet.org/) in pure Scala, with integrated
fonts, minimal dependencies, extensive error reporting and support for effects including Cats `IO`.

> FIGlet is a computer program that generates text banners, in a variety of typefaces, composed of
> letters made up of conglomerations of smaller ASCII characters (see ASCII art). The name derives
> from "Frank, Ian and Glenn's letters".

## Setup

Supports Scala version 2.13.x and 2.12.x.

Not released on Maven Central yet.

## Quick start

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

  // Obtain an options builder
  val builder = Figlet4s.builder()

  // Render a text into a FIGure
  val figure = builder.render("Hello, World!")

  // Print the FIGure
  figure.print()

}
```

### Setting options

In this example we see some options that you can configure, and we see a more compact way of making
the calls, without storing objects at each step.

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object ShowcaseOptionsMain extends App {

  Figlet4s
    .builder("Hello, World!")      // Create the options builder with a text to render
    .withMaxWidth(80)              // Max width of the text
    .withInternalFont("calgphy2")  // Set the font
    .defaultMaxWidth()             // Go back to the default max  width
    .withHorizontalLayout(
      HorizontalLayout.FullWidth   // Choose a layout
    )
    .text("Hello, Scala!")         // Change the text to render
    .render()                      // Render the text to a FIGure
    .print()                       // Print the FIGure

}
```

### Using the Figlet4s client

If you need to have more fine-grained control on the operations, or you prefer to not use the option
builder, you can call the API primitives yourself to fill the `RenderOptions` case class that is
used to pass, as the name suggests, the options to render a text.

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object LowLevelMain extends App {

  // Load a font, choose the layout and max width
  val font           = Figlet4s.loadFontInternal("calgphy2")
  val maxWidth       = 120
  val layout         = HorizontalLayout.HorizontalFitting
  val printDirection = PrintDirection.LeftToRight

  // Build the render options
  val options = RenderOptions(font, maxWidth, layout, printDirection)

  // Render a string into a FIGure
  val figure = Figlet4s.renderString("Hello, World!", options)

  // Print the FIGure
  figure.print()

}
```

## Using Effects

The API of the core Figlet4s library are impure (like loading a font from a file), and the functions
return pure values (like a `FIGfont`) as well as throwing exception when errors occur.

The `figlet4s-effects` package adds support for various effects. In the current version, Figlet4s
effects supports:

* Scala `Either` where the `Left` side stores the errors and exceptions
* Cats' `IO` where `MonadError` stores the errors and exceptions

that can be used by importing the corresponding package. The effectful API have exactly the same
signature as their unsafe version, but the result is wrapped inside the effect monad.

### Example using Cats IO

```scala
import cats.effect._
import com.colofabrix.scala.figlet4s.catsio._

// Note that I'm using Cats' IOApp here instead of Scala's App
object IOMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      builder <- Figlet4s.builderF()             // Obtain an options builder
      figure  <- builder.render("Hello, World!") // Render a text into a FIGure
      _       <- figure.print()                  // Print the FIGure
    } yield ExitCode.Success

}
```

### Example using Scala Either

```scala
import com.colofabrix.scala.figlet4s.either._

object EitherMain extends App {

  val result =
    for {
      builder <- Figlet4s.builderF()             // Obtain an options builder
      figure  <- builder.render("Hello, World!") // Render a text into a FIGure
      lines   <- figure.asSeq()                  // Store the FIGure as lines in a variable
    } yield lines

  // Interpreting the result
  result match {
    case Left(error)  => println(s"Error while working with FIGlet: $error")
    case Right(value) => value.foreach(println)
  }

}
```

## Glossary of Figlet4s terms

Figlet4s defines several concepts that broadly correspond to the ones defined in the [The FIGfont
Version 2 FIGfont and FIGdriver Standard](figfont_reference.txt) but in this library they might
assume a more specific meaning.

**FIGfont**

A FIGlet Font is a map of characters to their FIG-representation, and the typographic settings used
to display them.

**FIGcharacter**

It's a single FIGlet character, part of a FIGfont, that maps a single `Char` to its FIGlet
representation, and it's composed of SubLines/SubColumns.

**FIGheader**

FIGLettering Font file header that contains the raw configuration settings for the FIGfont.

**FIGure**

A FIGure is `String` rendered with a specific FIGfont ultimately built by concatenating and merging
FIGcharacters following a specific layout.

**SubLine and SubColumn**

Represents the SubLines/SubColumns in Figlet which are the String that compose each line/column of
the FIGure or of a FIGcharacter.

## Planned features, TODOs and Bugs

### Features

* Support for control files `*.flc`
* Support for zipped fonts
* Support for right-to-left
* Support for vertical layout

### TODO

* SBT code to deploy on maven central
* Add support for Scala 2.12
* Create and deploy proper Scaladoc documentation for API
* Create a docsite
* Command line interpreter

## License

MIT

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
