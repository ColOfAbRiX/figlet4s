# Figlet4s

This is an implementation of [FIGlet](http://www.figlet.org/) in pure Scala with integrated fonts,
support for Cats and minimal dependencies.

This implementation follows the rules established in the [The FIGfont Version 2 FIGfont and
FIGdriver Standard](figfont_reference.txt).

Its interface is based on the unix command `figlet` and takes ideas from the Python library
`PyFiglet`.

## Example using impure functions

### Quick start

This simple example shows step-by-step how to use Figlet4s.

You first obtain an option builder that is used to set the options you want. After that you request the renderer to
convert the text into a FIGure that can be printed at the end.

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object QuickStartMain extends App {

  // Obtain an options builder with the default options and a set text
  val builder = Figlet4s.builder("Hello, World!")

  // Render the text into a FIGure using the default options
  val figure = builder.render()

  // Print the FIGure
  figure.print()
 
  // The rendered FIGure as a single string or as a list of strings (one for each line)
  val renderedText = figure.asString()
  val renderedLines = figure.asVector() 

}
```

### Showcasing options

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

object ShowcaseOptionsMain extends App {

  Figlet4s
    .builder("Hello, World!")                       // Create the options builder
    .withMaxWidth(80)                               // Max width of the text
    .withInternalFont("alligator")                  // Set the font
    .withHorizontalLayout(HorizontalFittingLayout)  // Choose a layout
    .defaultMaxWidth()                              // Go back to the default max width
    .text("Hello, Scala!")                          // Using a different text
    .render()                                       // Render the text to a FIGure
    .print()                                        // Print the FIGURE

}
```

Full list of settings:

* `text`: Use the specified Text Width 
* `defaultFont`: Use the default FIGfont 
* `withInternalFont`: Use the internal FIGfont with the specified fontName 
* `withFont`: Use the FIGfont with the specified fontPath 
* `withFont`: Use the specified FIGfont 
* `defaultHorizontalLayout`: Use the default Horizontal Layout 
* `withHorizontalLayout`: Use the specified Horizontal Layout 
* `defaultMaxWidth`: Use the default Max Width 
* `withMaxWidth`: Use the specified Max Width 

## Example using cat's IO

### Quick start

This is the same example as above, only using IOApp

```scala
import cats.effect.IOApp
import com.colofabrix.scala.figlet4s.catsio._

object QuickStartIOMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      builder <- Figlet4s.builderF("Hello, World!")
      figure  <- builder.render()
      _       <- figure.print()
    } yield ExitCode.Success

}
```

### Showcasing options

This is the same example as above, only using IOApp. You can find a more comprehensive list of options in the example
above

```scala
import cats.effect.IOApp
import com.colofabrix.scala.figlet4s.catsio._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

object ShowcaseOptionsIOMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      figure <- Figlet4s
                  .builder("Hello, World!")                      // Create the options builder
                  .withMaxWidth(80)                              // Max width of the text
                  .withInternalFont("alligator")                 // Set the font
                  .withHorizontalLayout(HorizontalFittingLayout) // Choose a layout
                  .defaultMaxWidth()                             // Go back to the default max width
                  .text("Hello, Scala!")                         // Using a different text
                  .render()                                      // Render the text to a FIGure
      _ <- figure.print()
    } yield ExitCode.Success

}
```

## Planned features

* Support for vertical layout
* Support for control files `*.flc`
* Support for zipped fonts
* Test and improve speed and memory performance

## License

MIT

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
