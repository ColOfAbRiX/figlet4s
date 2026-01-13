---
layout: docs
title: Setup
---
# Setup

Figlet4s requires at least Java 8 and supports Scala 3 and 2.13.

## SBT

When using SBT, add the following line to your build file:

```scala
// Core library
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-core" % "@VERSION@"
```

If you want support for effects (see the section [Using
effects](advanced-usage.html#using-effects)) you also need to include a second package:

```scala
// Effects extension package
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-effects" % "@VERSION@"
```

If you want to use Figlet4s in a pure Java project you can import the Java package:

```scala
// Note that this line uses only a single `%` between the group id and the artifact id.
libraryDependencies += "com.colofabrix.scala" % "figlet4s-java" % "@VERSION@"
```

## Ammonite

To play with Figlet4s on [Ammonite](http://ammonite.io/), use the following import inside the
Ammonite REPL or add that same line inside Ammonite's user file `~/.ammonite/predef.sc`:

```scala
import $ivy.`com.colofabrix.scala::figlet4s-core:@VERSION@`
import $ivy.`com.colofabrix.scala::figlet4s-effects:@VERSION@`
```

## Maven for Java

If you're using Maven you're probably looking to use Figlet4s inside Java. This is what to use with
maven:

```xml
<dependency>
  <groupId>com.colofabrix.scala</groupId>
  <artifactId>figlet4s-java</artifactId>
  <version>@VERSION@</version>
</dependency>
```
