---
layout: docs
title: Getting started
---
# Using Figlet4s

These examples show step-by-step how to use Figlet4s. This is a basic scenario where we assume the
users don't want to use or deal with effects, and it's acceptable that errors are thrown as
exceptions.

The general way to use Figlet4s involves 3 steps:

1. Obtain a builder to set the options.
2. Configure the options of the builder, if needed.
3. Render a text into a `FIGure`

A `FIGure` is the object that holds the rendered text. Once you have a `FIGure` you can do further
processing like printing it or converting it to a string.

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object Main extends App {

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

object Main extends App {

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
you  can also obtain the displayable lines `SubLines`, that compose the FIGure as a collection and
then you can manipulate it.

The logo of Figlet4s has been created with this code:

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object Main extends App {

  Figlet4s
    .builder("Figlet4s")
    .render()
    .asSeq()
    .zipWithIndex
    .foreach { case (line, i) =>
      if (i == 4) println(s"$line  ASCII-art banners, in Scala")
      else println(line)
    }

}
```

## Listing all fonts

You can easily list all the fonts that are shipped with Figlet4s with a simple call to
`Figlet4s.internalFonts`.

The following code displays a text in each available internal font (beware, it will be a pretty long
output!):

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object Main extends App {

  for (font <- Figlet4s.internalFonts) {
    Figlet4s
      .builder("Hello, World!")
      .withInternalFont(font)
      .render()
      .print()
  }

}
```

## Examples of Horizontal Layouts

The horizontal layout is how figlet lays out each individual character.

### Full width layout

The full width layout displays all FIGcharacters at their full width, which may be fixed or
variable, depending on the font.

Code:

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object Main extends App {

  Figlet4s
    .builder("Hello, World!")
    .withHorizontalLayout(HorizontalLayout.FullWidth)
    .render()
    .print()

}
```

Result:

```plaintext
  _   _          _   _                __        __                 _       _   _
 | | | |   ___  | | | |   ___         \ \      / /   ___    _ __  | |   __| | | |
 | |_| |  / _ \ | | | |  / _ \         \ \ /\ / /   / _ \  | '__| | |  / _` | | |
 |  _  | |  __/ | | | | | (_) |  _      \ V  V /   | (_) | | |    | | | (_| | |_|
 |_| |_|  \___| |_| |_|  \___/  ( )      \_/\_/     \___/  |_|    |_|  \__,_| (_)
                                |/
```

### Horizontal fitting layout (kerning)

As many blanks as possible are removed between FIGcharacters, so that they touch, but the
FIGcharacters are not smushed.

Code:

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object Main extends App {

  Figlet4s
    .builder("Hello, World!")
    .withHorizontalLayout(HorizontalLayout.HorizontalFitting)
    .render()
    .print()

}
```

Result:

```plaintext
 _   _        _  _           __        __            _      _  _
| | | |  ___ | || |  ___     \ \      / /___   _ __ | |  __| || |
| |_| | / _ \| || | / _ \     \ \ /\ / // _ \ | '__|| | / _` || |
|  _  ||  __/| || || (_) |_    \ V  V /| (_) || |   | || (_| ||_|
|_| |_| \___||_||_| \___/( )    \_/\_/  \___/ |_|   |_| \__,_|(_)
                         |/
```

### Horizontal smushing layout

The FIGcharacters are displayed as close together as possible, and overlapping sub-characters are
removed. Exactly which sub-characters count as overlapping depends on the font's layout mode, which
is defined by the font's author. It will not smush a font whose author specified kerning or full
width as the default layout mode.

Code:

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object Main extends App {

  Figlet4s
    .builder("Hello, World!")
    .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)
    .render()
    .print()

}
```

Result:

```plaintext
 _   _      _ _         __        __         _     _ _
| | | | ___| | | ___    \ \      / /__  _ __| | __| | |
| |_| |/ _ \ | |/ _ \    \ \ /\ / / _ \| '__| |/ _` | |
|  _  |  __/ | | (_) |    \ V  V / (_) | |  | | (_| |_|
|_| |_|\___|_|_|\___( )    \_/\_/ \___/|_|  |_|\__,_(_)
                    |/
```
