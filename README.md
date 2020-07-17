# Figlet4s

This is an implementation of [FIGlet](http://www.figlet.org/) in pure Scala with integrated fonts,
support for Cats and minimal dependencies.

This implementation follows the rules established in the [The FIGfont Version 2 FIGfont and
FIGdriver Standard](figfont_reference.txt).

Its interface is based on the unix command `figlet` and takes ideas from the Python library
`PyFiglet`.

## Example

```scala
import com.colofabrix.scala.figlet4s._

object Main extends App {
  // Simple "Hello world" with default font (standard)
  Figlet4s.renderString("Hello, World!").print()

  // Setting options
  val alligatorFont80 = RenderOptionsBuilder()
    .withInternalFont("alligator")
    .withMaxWidth(80)

  // Selecting a specific internal font
  Figlet4s.renderString("Hello, World!", alligatorFont80).print()

  // List the available internal fonts
  Figlet4s.internalFonts.foreach(println)

  // Loading and printing a font
  val aCustomFont = RenderOptionsBuilder()
    .withFont("path/to/font.flf")
  Figlet4s.renderString("Hello, World!", aCustomFont).print()
}
```

## Planned features

* Support for generic effects
* Support for vertical layout
* Support for control files `*.flc`
* Support for zipped fonts
* Test and improve speed and memory performance

## License

MIT

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
