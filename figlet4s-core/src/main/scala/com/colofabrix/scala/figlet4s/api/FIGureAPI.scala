package com.colofabrix.scala.figlet4s.api

/**
 * Common interface of all implementations of option builders
 */
trait FIGureAPI[F[_]] {
  /** Apply a function to each line of the FIGure */
  def foreachLine[A](f: String => A): F[Unit]

  /** Print the FIGure */
  def print(): F[Unit]

  /** The figure as a collection of String, one String per displayed line */
  def asVector(): Vector[String]

  /** The figure as single String and newline characters */
  def asString(): String
}

/**
 * Common interface of all implementations of option builders that wrap results inside an effect
 */
trait FIGureEffectfulAPI[F[_]] {
  /** Apply a function to each line of the FIGure */
  def foreachLine[A](f: String => A): F[Unit]

  /** Print the FIGure */
  def print(): F[Unit]

  /** The figure as a collection of String, one String per displayed line */
  def asVectorF(): F[Vector[String]]

  /** The figure as single String and newline characters */
  def asStringF(): F[String]
}
