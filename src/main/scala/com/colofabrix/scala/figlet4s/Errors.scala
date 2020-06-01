package com.colofabrix.scala.figlet4s

/**
 * FIGLet Error
 */
sealed trait FigletError extends Throwable

/**
 * Errors for FIGLet Files
 */
sealed trait FLFError extends FigletError

/** FLF Header Error */
final case class FlfHeaderError(message: String) extends FLFError

/** FLF Character Error */
final case class FlfCharacterError(message: String) extends FLFError

/** FLF Font Error */
final case class FlfFontError(message: String) extends FLFError
