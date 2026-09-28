[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://choosealicense.com/licenses/mit/)
[![Maven Central](https://img.shields.io/maven-central/v/com.colofabrix.scala/figlet4s-core_3.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.colofabrix.scala/figlet4s-core_3)
[![Tests Status](https://github.com/ColOfAbRiX/figlet4s/actions/workflows/tests.yml/badge.svg)](https://github.com/ColOfAbRiX/figlet4s/actions/workflows/tests.yml)

```
 _____ _       _      _   _  _
|  ___(_) __ _| | ___| |_| || |  ___
| |_  | |/ _` | |/ _ \ __| || |_/ __|
|  _| | | (_| | |  __/ |_|__   _\__ \
|_|   |_|\__, |_|\___|\__|  |_| |___/  ASCII-art banners, in Scala
         |___/
```

# Figlet4s

Figlet4s is a library to create ASCII-art banners, in pure Scala, open source, with integrated fonts,
minimal dependencies (cats and cats-effects), extensive error reporting and support for effects
including Cats `IO`. It also includes direct support for Java.

Try out [Figlet4s on Scastie](https://scastie.scala-lang.org/fzrGAYvLQ5uP0xiQ2HTjsw)

## Quick Start

Add the core library to your `build.sbt`:

```scala
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-core" % "0.4.0"
```

Then render your first banner:

```scala
import com.colofabrix.scala.figlet4s.unsafe._

val figure = Figlet4s.builder().text("Hello, World!").render()

println(figure.asString)
```

Full options and effectful APIs (cats `IO`, `Either`) are described in the
[documentation](https://colofabrix.github.io/figlet4s/docs/using-figlet4s.html).

## Documentation

Go to [https://colofabrix.github.io/figlet4s/](https://colofabrix.github.io/figlet4s/)

## Changelog

You can check [the changelog of Figlet4s here](CHANGELOG.md)

## What can I use Figlet4s for?

FIGlet is mainly a decorative application. But this doesn't mean it's not useful! You can use it to
add some touch to your applications or environments!

Some example applications are:

* A banner for your logs
* Decorate your terminal, like with great login message on terminals
* Develop your own text-base adventure game
* ASCII-art for retro-looking websites
* [There are plenty of projects that use Figlet!](https://github.com/topics/figlet)

## License

Figlet4s is released under a "MIT" license. See [LICENSE](LICENSE) for specifics and copyright
declaration.

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
