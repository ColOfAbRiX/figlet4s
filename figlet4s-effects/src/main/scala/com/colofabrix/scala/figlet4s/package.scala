package com.colofabrix.scala

/**
 * The API of the core Figlet4s library are impure (like loading a font from a file), and the functions
 * return pure values (like a FIGfont) as well as throwing exception when errors occur.
 *
 * The `figlet4s-effects` package adds support for various effects. In the current version, Figlet4s
 * effects supports:
 *
 * * Scala `Either` where the `Left` side stores the errors and exceptions
 * * Cats' `IO` where `MonadError` stores the errors and exceptions
 *
 * that can be used by importing the corresponding package. The effectful API have exactly the same
 * signature as their unsafe version, but the result is wrapped inside the effect monad.
 */
package object figlet4s {}
