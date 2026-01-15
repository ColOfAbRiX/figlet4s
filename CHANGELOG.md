# Changelog

## Unreleased

### Added

* Added BDF Fonts
* Added wartremover-contrib

### Breaking Changes

* **Dropped Scala 2.12 support** - Now supports Scala 3.6.3 and 2.13.16 only
* **Upgraded to Cats Effect 3** - Migrated from Cats Effect 2.x to 3.5.7

### Changed

* Upgraded to Scala 3.6.3 as primary version, cross-compiled with 2.13.16
* Upgraded to Cats 2.13.0 (from 2.6.1)

### Dependencies Changes

* Added new cats-effect-testing-scalatest: 1.6.0
* Upgraded to cats-core: 2.13.0
* Upgraded to cats-effect: 3.5.7
* Upgraded to cats-kernel: 2.13.0
* Upgraded to cats-scalatest: 4.0.2 (for Scala 3)
* Upgraded to enumeratum: 1.7.5
* Upgraded to scalacheck: 1.18.1
* Upgraded to scalamock: 6.0.0
* Upgraded to scalatest: 3.2.19

### Build & Plugin Changes

* Added new sbt-dynver 5.1.0
* Added new sbt-native 1.10.4
* Added new sbt-tpolecat 0.5.2
* Added new sbt-unidoc: 0.5.0
* Upgraded to sbt: 1.12.0
* Upgraded to sbt-assembly: 2.3.0
* Upgraded to sbt-explicit-dependencies: 0.3.1
* Upgraded to sbt-git: 2.1.0
* Upgraded to sbt-microsites: 1.4.4
* Upgraded to sbt-pgp: 2.3.1
* Upgraded to sbt-scalafix: 0.14.5
* Upgraded to sbt-scalafmt: 2.5.6 (config version: 3.8.4)
* Upgraded to sbt-scoverage: 2.3.0
* Upgraded to sbt-sonatype: 3.12.2
* Upgraded to sbt-updates: 0.6.4
* Upgraded to sbt-wartremover: 3.2.5

## v0.3.2 - 2021/11/05

### Bugfixes

* Fixed "Internal font list on Windows still uses "\" path sep (#21)"

## v0.3.1 - 2021/11/13

### Bugfixes

* Fixed "Internal font loading fails on Windows (#20)"
* Fixed line termination issues on Windows

## v0.3.0 - 2021/04/07

### Added

* Support for zipped font files
* Figlet4s internal fonts can now be organised in directroies
* Added Commodore64 FIGfonts

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
