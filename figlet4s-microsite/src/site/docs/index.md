---
title: Intro
---
# Introduction

Figlet4s is a library to create ASCII-art banners, in pure Scala, open source, with integrated fonts,
minimal dependencies, extensive error reporting and support for effects including Cats `IO`. It also
includes direct support for Java.

Figlet4s is an implementation of the *nix tool [FIGlet](http://www.figlet.org/):

> FIGlet is a computer program that generates text banners, in a variety of typefaces, composed of
> letters made up of conglomerations of smaller ASCII characters (see ASCII art). The name derives
> from "Frank, Ian and Glenn's letters".
>
> -- _Wikipedia_

Try out [Figlet4s on Scastie](https://scastie.scala-lang.org/fzrGAYvLQ5uP0xiQ2HTjsw) or jump to the
@ref:[Getting started](using-figlet4s.md) section.

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

* [Core library](https://colofabrix.github.io/figlet4s/api/com/colofabrix/scala/figlet4s/unsafe.html)
* [Effects support library](https://colofabrix.github.io/figlet4s/api/com/colofabrix/scala/figlet4s.html)
* [Java wrapper library](https://colofabrix.github.io/figlet4s/api-java/overview-summary.html)

## Missing features

Figlet4s doesn't implement all figlet features yet, although they are in the plan. The following
are the missing features:

* Vertical alignment
* Control files

## License

Figlet4s is released under a "MIT" license.
