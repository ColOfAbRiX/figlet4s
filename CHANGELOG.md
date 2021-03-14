# Changelog

## Unreleased

### Added

* Support for zipped font files
* Figlet4s internal fonts can now be organised in directories
* Added new Commodore64 FIGfonts

### Changed

* Set the default Max Width to 80, same as original figlet
* Updated SBT to 1.4.7

### Bugfixes

* Fixed path issues on different platforms
* Fixed handling of resources (reading files)
* Fixed inconsistent error reporting on file opening

## v0.2.0 - 2020/12/14

### Added

* Added direct support for Java

### Changed

* Errors are returned in a more uniform format defined in the library
* Removed test dependencies from POM
* Updated cats-core and cats-effect to version 2.3.0
* Updated SBT to 1.4.4
* Updated testing libraries: ScalaTest 3.2.3, ScalaCheck 1.15.0, Cats-ScalaTest 3.1.1

## v0.1.0 - 2020/11/13

Initial release.

The initial features of the first release are:

* Rendering of figures is identical to the original FIGlet
* Support for FLF font definitions files in plain text
* Included all the main FIGlet fonts (some of which have been fixed)
* All figlet horizontal layout supported (only basic vertical layout supported)
* Default API provides direct access to the return values with exceptions thrown
* Extension package adds support for Scala's `Either` and Cats' `IO`
* Extensive error reporting when parsing font files
* Builder used to avoid dealing directly with each and every option
* Scaladoc for all the API
* Support for Scala 2.13.x and 2.12.x
