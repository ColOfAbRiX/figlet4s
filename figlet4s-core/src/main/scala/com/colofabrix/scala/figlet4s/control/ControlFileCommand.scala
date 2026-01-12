package com.colofabrix.scala.figlet4s.control

import com.colofabrix.scala.figlet4s.utils

/**
 * A command in a Figlet Control File
 */
sealed trait ControlFileCommand extends utils.ADT

/**
 * An extended command in a Figlet Control File.
 *
 * They control how a FIGdriver interprets bytes in the input. By default, the FIGdriver interprets each byte of input
 * as a distinct character. All these commands are logically acted on before any other control file commands, no matter
 * where in the sequence of control files they appear. They are also mutually exclusive; if more than one of these
 * commands is found, only the last is acted on. Multiple "g" commands are permitted, however.
 */
sealed trait ExtendedControlFileCommand extends ControlFileCommand

object ControlFileCommand {

  /**
   * The first type of "t" command transforms characters with the code "inchar" into characters with the code "outchar".
   * The second type of "t" command transforms characters in the range "inchar1" to "inchar2" as the corresponding codes
   * in the range "outchar1" to "outchar2". Both ranges must be of the same size. The form "number number" is
   * equivalent to a "t" command of the first type, and is provided for compatibility with the mapping tables issued by
   * the Unicode Consortium.
   *
   * @param inchar The input character to be mapped
   * @param outchar The output character after the mapping
   */
  final case class TCommand(inchar: String, outchar: String) extends ControlFileCommand

  /**
   * Multiple transformation stages can be encoded in a single control file by using "f" commands to separate the
   * stages.
   */
  case object FCommand extends ControlFileCommand

  /**
   * The "h" command forces the input to be interpreted in HZ mode, which is used for the HZ character encoding of
   * Chinese text.
   */
  case object HCommand extends ExtendedControlFileCommand

  /**
   * The "j" command forces the input to be interpreted in Shift-JIS mode (also called "MS-Kanji mode")
   */
  case object JCommand extends ExtendedControlFileCommand

  /**
   * The "b" command forces the input to be interpreted in DBCS mode, which is suitable for processing HZ or Shift-GB
   * Chinese text or Korean text.
   */
  case object BCommand extends ExtendedControlFileCommand

  /**
   * The "u" command forces the input to be interpreted in UTF-8 mode, which causes any input byte in the range 0x80 to
   * 0xFF to be interpreted as the first byte of a multi-byte Unicode (ISO 10646) character.
   */
  case object UCommand extends ExtendedControlFileCommand

  /**
   * The "g" command provides information for the ISO 2022 interpreter
   */
  final case class GCommand(set: Int) extends ExtendedControlFileCommand

}
