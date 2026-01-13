package com.colofabrix.scala.figlet4s.api

/**
 * Common interface of all implementations of option builders
 *
 * @tparam F The effect that wraps the results of the functions
 */
trait FIGureAPI[F[_]] {

  /**
   * Apply a function to each line of the FIGure
   *
   * @param f The function to applied to each displayable line
   */
  def foreachLine[A](f: String => A): F[Unit]

  /**
   * Print the FIGure
   */
  def print(): F[Unit]

  /**
   * The figure as a collection of String, one String per displayable line
   *
   * @return A collection of strings, each containing a displayable line
   */
  def asSeq(): Seq[String]

  /**
   * The figure as single String and newline characters
   *
   * @return A single string containing the FIGure including newlines where needed
   */
  def asString(): String

}

/**
 * Common interface of all implementations of option builders that wrap all results of functions inside an effect
 *
 * @tparam F The effect that wraps the results of the functions
 */
trait FIGureEffectfulAPI[F[_]] {

  /**
   * Apply a function to each line of the FIGure
   *
   * @param f The function to applied to each displayable line
   */
  def foreachLine[A](f: String => A): F[Unit]

  /**
   * Print the FIGure
   */
  def print(): F[Unit]

  /**
   * The figure as a collection of String, one String per displayable line
   *
   * @return A collection of strings, each containing a displayable line
   */
  def asSeqF(): F[Seq[String]]

  /**
   * The figure as single String and newline characters
   *
   * @return A single string containing the FIGure including newlines where needed
   */
  def asStringF(): F[String]

}
