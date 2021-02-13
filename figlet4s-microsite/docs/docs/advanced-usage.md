---
layout: docs
title: Advanced usage
---
# Advanced usage

## Using the Figlet4s client

If you need to have more fine-grained control on the operations, or you prefer to not use the option
builder, you can call the API primitives yourself to fill the `RenderOptions` case class that is
used to pass, as the name suggests, the options to render a text.

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.options._

object Main extends App {

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

## Handling Errors

In functional programming throwing exception is considered not acceptable because it breaks
something called referential transparency, which causes a lot of other issues that FP developers
want to avoid. The standard way to solve this problem is to use a functional pattern called monads.
Functions that throw exceptions are called *impure* and the code is said to be *unsafe*, on the
other hand functions tha use monads are said to be *effectful* (where the effect is the particular
monad used).

In Figlet4s all the calls to the methods that load fonts can result in an error and this includes
calls to load internal fonts too.

Internally, Figlet4s is designed with a purely functional core that exposes a generic interface (not
visible to the library user). The generic interface is made available to the library user with an
impure implementation that can throw exceptions and with an additional implementation that uses
functional effects in the `figlet4s-effects` library. The impure version is meant for quick
prototyping or the developers that don't desire to use the functional approach.

The unsafe methods and the effectful methods are defined in two types of Scala traits, one for
each type. You can find the list and documentation of these traits [on this page
here](https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-core_@SCALA_VERSION@/@VERSION@/figlet4s-core_@SCALA_VERSION@-@VERSION@-javadoc.jar/!/com/colofabrix/scala/figlet4s/api/index.html).

### Unsafe calls

When we try to load an internal font that doesn't exist we expect Figlet4s to throw an exception



### Using Effects

The `figlet4s-effects` package adds support for effects to Figlet4s. In the current version
@VERSION@, Figlet4s effects supports:

* Scala `Either` where the `Left` side stores the errors and exceptions
* Cats' `IO` where `MonadError` stores the errors and exceptions

that can be used by importing the corresponding package. The effectful API have exactly the same
signature as their unsafe version, but the result is wrapped inside the effect monad.

When using the effect library extension, new methods will be available that return a monadic result
and that are postfixed by a capital `F`. For example you'll be able to use two methods to create
a builder:

```scala
// Unsafe version
def builder(text: String): OptionsBuilder

// Monadic version
def builderF(text: String): F[OptionsBuilder]
```

### Example using Cats IO

```scala
import cats.effect._
import com.colofabrix.scala.figlet4s.catsio._

// Note that I'm using Cats' IOApp here instead of Scala's App
object Main extends IOApp {

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

object Main extends App {

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
