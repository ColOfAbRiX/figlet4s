---
layout: docs
title: Setup
permalink: docs/setup
---
# Setup

Figet4s requires at least Java 8 and supports Scala versions 2.13 and 2.12.

## SBT

When using SBT, add the following line to your build file:

```scala
// Core library
libraryDependencies += "com.colofabrix.scala" %% "figlet4s-core" % "@VERSION@"
```

If you want support for effects (see the section [Using
effects](../../docs/advanced-usage/#using-effects)) you also have to include a second package:

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

If you want to play with Figlet4s on Ammonite, use the following import inside the Ammonite REPL:

```scala
import $ivy.`com.colofabrix::figlet4s-core:@VERSION@`
```

or add that same line inside Ammonite's user file `~/.ammonite/predef.sc`

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
