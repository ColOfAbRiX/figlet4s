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

Figlet4s is a pure Scala, open source, library implementation of the ASCII-art program
[FIGlet](http://www.figlet.org/), with integrated fonts, minimal dependencies, extensive error
reporting and support for effects including Cats `IO`. It also includes direct support for Java.

But what is Figlet?

> FIGlet is a computer program that generates text banners, in a variety of typefaces, composed of
> letters made up of conglomerations of smaller ASCII characters (see ASCII art). The name derives
> from "Frank, Ian and Glenn's letters".
>
> -- _Wikipedia_

Try out [Figlet4s on Scastie](https://scastie.scala-lang.org/PACf1v7ZSlGSVQkkBef0Mg) or jump to the
[Quick Start](#quick-start)

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

### Requirements

Figet4s requires at least Java 8 and supports Scala versions 2.13 and 2.12.

### SBT

When using SBT, add the following line to your build file (for the latest release version, see the
Maven badge at the top of the readme):

```scala
// Core library
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-core" % <version>
```

If you want support for effects (see [below](#using-effects)) you also have to include a second
package:

```scala
// Effects extension package
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-effects" % <version>
```

If you want to use Figlet4s in a pure Java project you can import the Java package:

```scala
// Java extension package
// Note that this line uses only a single `%` between the group id and the artifact id.
libraryDependencies += "com.colofabrix.scala" % "figlet4s-java" % <version>
```

### Maven for Java

If you're using Maven you're probably looking to use Figlet4s inside Java. This is what to use with
maven:

```xml
<dependency>
  <groupId>com.colofabrix.scala</groupId>
  <artifactId>figlet4s-java</artifactId>
  <version><!-- version --></version>
</dependency>
```

Where `version` is the Figlet4s version (for the latest release version, see the Maven
badge at the top of the readme).

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

### Playing with the FIGure

When the FIGure has been rendered you can play with it. We've seen how to print it to screen but you
can also obtain the displayable lines (SubLines) that compose the FIGure as a collection and then
you can manipulate it.

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
  // These settings are present but not working yet, as per release 0.2.0
  val printDirection = PrintDirection.LeftToRight
  val justification  = Justification.FontDefault

  // Build the render options
  val options = RenderOptions(font, maxWidth, layout, printDirection, justification)

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

## Pure Java project

If you want to use Figlet4s on Java you can benefit from the thin wrapper developed for this purpose
and bundled in the `figlet4s-java` library. Using a Scala library from Java is very simple and
almost pain-free but there is still some manual conversion you have to do, especially with
collections, and some details you need to know like how to call a Scala `object` or extension
methods. `figlet4s-java` does the tricky bits for you.

Make sure you include in your project the correct dependencies (see the [Setup](#maven-for-java) for
the details).

Let's see the same examples we've seen above but this time from a pure Java perspective. This is the
example that uses the builder, in Java:

```java
import com.colofabrix.java.figlet4s.*;
import com.colofabrix.java.figlet4s.options.*;

public class Main {
    public static void main(String [] args) {
        Figlet4s
            .builder()
            .withHorizontalLayout(HorizontalLayout.HORIZONTAL_FITTING)
            .render("Hello, World!")
            .print();
    }
}
```

And this one is the one that sets explicitly each option:

```java
import com.colofabrix.java.figlet4s.*;
import com.colofabrix.java.figlet4s.options.*;

public class Main {
    public static void main(String [] args) {
        RenderOptions options = new RenderOptions(
            Figlet4s.loadFontInternal("standard"),
            120,
            HorizontalLayout.HORIZONTAL_FITTING,
            PrintDirection.LEFT_TO_RIGHT,
            Justification.FONT_DEFAULT);

        Figlet4s
            .renderString("Hello, World!", options)
            .print();
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

[1]: https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-core_2.13/0.2.0/figlet4s-core_2.13-0.2.0-javadoc.jar/!/com/colofabrix/scala/figlet4s/unsafe/index.html
