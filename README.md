[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://choosealicense.com/licenses/mit/)
[![Maven Central](https://img.shields.io/maven-central/v/com.colofabrix.scala/figlet4s-core_2.13.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.colofabrix.scala%22%20AND%20a:%22figlet4s-core_2.13%22)
[![Actions Status](https://github.com/ColOfAbRiX/figlet4s/workflows/tests/badge.svg)](https://github.com/ColOfAbRiX/figlet4s/actions)
[![Gitter chat](https://badges.gitter.im/ColOfAbRiX/figlet4s.png)](https://gitter.im/figlet4s/community)

```
 _____ _       _      _   _  _
|  ___(_) __ _| | ___| |_| || |  ___
| |_  | |/ _` | |/ _ \ __| || |_/ __|
|  _| | | (_| | |  __/ |_|__   _\__ \
|_|   |_|\__, |_|\___|\__|  |_| |___/  Figlet, but in Scala
         |___/
```

# Figlet4s

This is a library implementation of [FIGlet](http://www.figlet.org/) in pure Scala, with integrated
fonts, minimal dependencies, extensive error reporting and support for effects including Cats `IO`.

> FIGlet is a computer program that generates text banners, in a variety of typefaces, composed of
> letters made up of conglomerations of smaller ASCII characters (see ASCII art). The name derives
> from "Frank, Ian and Glenn's letters".
>
> -- _Wikipedia_

Try out [Figlet4s on Scastie](https://scastie.scala-lang.org/W6mH3brNSty1TeJeCRrOxg) or jump to the
[Quick Start](#quick-start)

## Features

Figlet4s is progressing to support all the features of the original command line figlet and, at the
moment, this is what Figlet4s can do:

* The rendering of figures is identical to the original FIGlet
* Supports FLF font definitions files in plain text
* Includes all the main FIGlet fonts (some of which have been fixed)
* All figlet horizontal layout supported (only vertical fitting layout supported)
* Default API provides direct access to the return values with exceptions thrown
* Extention package adds support for Scala's `Either` and Cats' `IO`
* A builder is provided to avoid to deal directly with each and every option
* Extensive and detailed error reporting
* Support for Scala 2.13 and 2.12

## What can I use Figlet4s for?

FIGlet is mainly a decorative application. But this doesn't mean it's not useful! You can use it to
add some touch to your applications or environments!

Some example applications are:

* A banner for your logs
* Decorate your terminal, like with great login message on terminals
* Develop your own text-base adventure game
* ASCII-art for retro-looking websites
* [There are plenty of projects that use Figlet!](https://github.com/topics/figlet)

## Setup

When using SBT, add the following line to your build file (for the latest release version, see the
Maven badge at the top of the readme):

```scala
// Core library
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-core" % <version>
```

If you want support for effects (see [below](#using-effects)) you also have to include a second
package:

```scala
// Effects extension
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-effects" % <version>
```

Figlet4s supports Scala version 2.13 and 2.12.

## Documentation

You can find the [API documentation here][1]

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

  // 1. Obtain an options builder
  val builder = Figlet4s.builder()

  // 2. In this example we use the default configuration

  // 3. Render a text into a FIGure
  val figure = builder.render("Hello, World!")

  // Do something with the FIGure
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
      builder <- Figlet4s.builderF()             // 1. Obtain an options builder
      figure  <- builder.render("Hello, World!") // 3. Render a text into a FIGure
      _       <- figure.print()                  // Do something with the FIGure
    } yield ExitCode.Success

}
```

### Example using Scala Either

```scala
import com.colofabrix.scala.figlet4s.either._

object EitherMain extends App {

  val result =
    for {
      builder <- Figlet4s.builderF()             // 1. Obtain an options builder
      figure  <- builder.render("Hello, World!") // 3. Render a text into a FIGure
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

## License

Figlet4s is released under a "MIT" license. See [LICENSE](LICENSE) for specifics and copyright
declaration.

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)

[1]: https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-core_2.12/0.1.0/figlet4s-core_2.12-0.1.0-javadoc.jar/!/com/colofabrix/scala/figlet4s/unsafe/index.html
