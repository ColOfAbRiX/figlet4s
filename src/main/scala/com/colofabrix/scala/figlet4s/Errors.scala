package com.colofabrix.scala.figlet4s

/**
 * FIGLet Error
 */
sealed trait FigletError

/**
 * Error for FIGLet Files
 */
final case class FLFError(message: String) extends FigletError
