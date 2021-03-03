---
layout: docs
title: Intro
---
# Introduction

Figlet4s is a pure Scala, open source, library implementation of the ASCII-art program
[FIGlet](http://www.figlet.org/), with integrated fonts, minimal dependencies, extensive error
reporting and support for effects including Cats `IO`. It also includes direct support for Java.

But what is Figlet?

> FIGlet is a computer program that generates text banners, in a variety of typefaces, composed of
> letters made up of conglomerations of smaller ASCII characters (see ASCII art). The name derives
> from "Frank, Ian and Glenn's letters".
>
> -- _Wikipedia_

Try out [Figlet4s on Scastie](https://scastie.scala-lang.org/9YY836k5SHmcrqeiNdL0pw) or jump to the
[Getting started](../../docs/using-figlet4s/) section.

## What can I use Figlet4s for?

FIGlet is mainly a decorative application. This doesn't mean it's not useful! You can use it to add
some touch to your applications or environments!

Some example applications are:

* A banner for your logs
* Decorate your terminal, like with great login message on terminals
* Develop your own text-base adventure game
* ASCII-art for retro-looking websites
* [There are plenty of projects that use Figlet!](https://github.com/topics/figlet)

## API Documentation

You can find the API documentation at the following links:

* [Core library](https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-core_@SCALA_VERSION@/@VERSION@/figlet4s-core_@SCALA_VERSION@-@VERSION@-javadoc.jar/!/com/colofabrix/scala/figlet4s/unsafe/index.html)
* [Effects support library](https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-effects_@SCALA_VERSION@/@VERSION@/figlet4s-effects_@SCALA_VERSION@-@VERSION@-javadoc.jar/!/com/colofabrix/scala/figlet4s/index.html)
* [Java wrapper library](https://oss.sonatype.org/service/local/repositories/releases/archive/com/colofabrix/scala/figlet4s-java/@VERSION@/figlet4s-java-@VERSION@-javadoc.jar/!/overview-summary.html)

## Missing features

Figlet4s doesn't implement all figlet features yet, although they are in the plan. The following
are the missing features:

* Print direction
* Justification
* Vertical alignment
* Control files

## License

Figlet4s is released under a "MIT" license.
