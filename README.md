# Figlet4s

This is an implementation of [FIGlet](http://www.figlet.org/) in pure Scala with integrated fonts,
support for Cats and minimal dependencies.

This implementation follows the rules established in the [The FIGfont Version 2 FIGfont and
FIGdriver Standard](figfont.txt).

Its interface is based on the unix command `figlet` and the Python library `PyFiglet`.

Example:

```scala
import com.colofabrix.scala.figlet4s._

object Main extends App {
  Figlet4s.renderString("Hello, World!").print()
}
```

## License

MIT

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
