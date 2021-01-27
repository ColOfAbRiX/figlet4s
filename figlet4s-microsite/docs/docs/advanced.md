---
layout: docs
title: Advanced usage
permalink: docs/advanced-usage
---
# Advanced usage

## Using the Figlet4s client

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
