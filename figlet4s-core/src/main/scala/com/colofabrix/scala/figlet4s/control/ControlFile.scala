package com.colofabrix.scala.figlet4s.control

import cats.implicits._
import com.colofabrix.scala.figlet4s.control.ControlFileCommand._
import com.colofabrix.scala.figlet4s.errors._

/**
 * A FIGfont control file is a separate text file, associated with one or more FIGfonts, that indicates how to map
 * input characters into FIGfont character codes.
 */
final case class ControlFile(
    sections: Seq[Seq[ControlFileCommand]] = Vector.empty,
    extended: Seq[ControlFileCommand] = Vector.empty,
)

object ControlFile {

  /**
   * Creates a new ControlFile by parsing an input collection of lines representing an FLC file
   *
   * @param lines An Iterable that contains all the lines representing an FLC file that defines the ControlFile
   * @return A [[com.colofabrix.scala.figlet4s.errors.FigletResult FigletResult]] containing the new ControlFile or a
   *         list of errors occurred during the creation
   */
  def apply(lines: Iterator[String]): FigletResult[ControlFile] =
    lines
      .map(_.trim())
      .filterNot(line => line.isEmpty || line.startsWith("#") || line.startsWith("flc2a"))
      .foldLeft(ControlFile().validNec[FigletException])(processLine)

  //  Support  //

  private def processLine(stateV: FigletResult[ControlFile], line: String): FigletResult[ControlFile] =
    stateV.andThen { state =>
      line.charAt(0) match {
        case 't' => parseTLine(line).andThen(appendCommands(state))
        case 'f' => addSection(state)
        case 'h' => appendExtendedCommand(state)(HCommand)
        case 'j' => appendExtendedCommand(state)(JCommand)
        case 'b' => appendExtendedCommand(state)(BCommand)
        case 'u' => appendExtendedCommand(state)(UCommand)
        case 'g' => parseGLine(line).andThen(appendExtendedCommand(state))
        case _   => parseUnknownLine(line).andThen(appendCommands(state))
      }
    }

  private def parseTLine(line: String): FigletResult[Seq[ControlFileCommand]] =
    ???

  private def parseGLine(line: String): FigletResult[ExtendedControlFileCommand] =
    ???

  private def parseUnknownLine(line: String): FigletResult[Seq[ControlFileCommand]] =
    ???

  private def appendCommands(state: ControlFile)(commands: Seq[ControlFileCommand]): FigletResult[ControlFile] =
    state
      .sections
      .lastOption
      .map(_ ++ commands)
      .map { updatedLast =>
        state.copy(sections = state.sections.dropRight(1) :+ updatedLast).validNec
      }
      .getOrElse(state.validNec)

  private def addSection(state: ControlFile): FigletResult[ControlFile] =
    state.copy(sections = state.sections ++ Seq.empty).validNec

  private def appendExtendedCommand(state: ControlFile)(
      command: ExtendedControlFileCommand,
  ): FigletResult[ControlFile] =
    state.copy(extended = state.extended :+ command).validNec

}
