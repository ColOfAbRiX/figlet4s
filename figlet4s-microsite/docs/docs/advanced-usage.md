---
layout: docs
title: Advanced usage
---
# Advanced usage

## Using the Figlet4s client directly

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

In Figlet4s all the calls to methods that load fonts can result in an error because they load from
files. This includes calls to load internal fonts too.

In functional programming throwing exception is considered not acceptable because it breaks a
property of code called referential transparency, which causes a lot of other issues that FP
developers want to avoid. The standard way to solve this problem is to use a functional pattern
called monads. Functions that throw exceptions are said to be *impure* and they are *unsafe* because
you can't control exceptions in the normal flow of the code (you would need to use try/catch). On
the other hand, functions tha use monads preserve referential transparency are said to be
*effectful* (where the effect is the particular monad used).

Figlet4s is designed with a purely functional core, not visible to the library user, that exposes a
generic interface. The `figlet4s-core` library implements the generic interface with unsafe methods
that can throw exceptions while the `figlet4s-effects` library provides an implementation that makes
use of monads and effects.

The unsafe methods and the effectful methods are defined in two types of Scala traits, one for
each type. You can find the list and documentation of these traits [on this page
here](https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-core_@SCALA_VERSION@/@VERSION@/figlet4s-core_@SCALA_VERSION@-@VERSION@-javadoc.jar/!/com/colofabrix/scala/figlet4s/api/index.html).

### Unsafe calls

The impure interface of Figlet4s is meant for quick prototyping or for developers that don't desire
to use the purely FP approach.

When you try to load an internal font that doesn't exist you expect Figlet4s to throw an exception.
In the following code we try to load a font that doesn't exist and the result is an exception as s

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object Main extends App {

  val myFont = Figlet4s.loadFontInternal("does-not-exist")

  // Exception: com.colofabrix.scala.figlet4s.errors$FigletLoadingError

}
```

You might want to deal with this yourself, using a try/catch or other techniques.

When using the builder, a call to load a font doesn't immediately throw an exception. Instead the
builder is more like a sequence of actions you want to perform and only at the end they are
executed. This means that you can play with the builder without fear of exceptions.

Let's see how this works in practice:

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object Main extends App {

  val builder =
    Figlet4s
      .builder("Error handling")
      .withInternalFont("does-not-exist") // This is safe and doesn't throw exceptions

  // The builder runs here and only here the exception is thrown
  val options = builder.options

  // Exception: com.colofabrix.scala.figlet4s.errors$FigletLoadingError

}
```

### Using Effects

The other way to handle errors is by using effects that wrap the result and possible errors inside a
container.

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

### Example using Scala Either

Scala's `Either` is a wrapper that can contain one of two values at the time called left and right.
By convention the right-side of an `Either` contains the result of your computation while the
left-side contains any error that occurred.

```scala
import com.colofabrix.scala.figlet4s.either._

object Main extends App {

  val result =
    for {
      builder <- Figlet4s.builderF()
      figure  <- builder.render("Hello, World!")
      lines   <- figure.asSeq()
    } yield lines

  // Handle errors and display result
  result match {
    case Left(error)  => println(s"Error while working with FIGlet: $error")
    case Right(value) => value.foreach(println)
  }

}
```

### Example using Cats IO

`IO` is a monad from the Cats library and it is used to represent a computation as a pure value.
When the computation inside `IO` throws or raises and exception, the errors is stored as a value
inside the monad, in a construct that is called `MonadError`. From there you can deal with the error
as if it was a value.

```scala
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.catsio._

// Note that I'm using Cats' IOApp here instead of Scala's App
object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val result =
      for {
        builder <- Figlet4s.builderF()
        figure  <- builder.render("Hello, World!")
        _       <- figure.print()
      } yield ExitCode.Success

      result.recover(handleError)
  }

  // Handle errors
  def handleError: PartialFunction[Throwable, ExitCode] = {
    case error: Throwable =>
      println(s"Error while working with FIGlet: $error")
      ExitCode.Error
  }

}
```
