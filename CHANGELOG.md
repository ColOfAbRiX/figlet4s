# Changelog

## Unreleased

### Changed

* Errors are returned in a more uniform format defined in the library
* Updated cats-core and cats-effect to version 2.3.0
* Removed test dependencies from POM

## v0.1.0 - 2020/11/13

Initial release.

The initial features of the first release are:

* Rendering of figures is identical to the original FIGlet
* Support for FLF font definitions files in plain text
* Included all the main FIGlet fonts (some of which have been fixed)
* All figlet horizontal layout supported (only basic vertical layout supported)
* Default API provides direct access to the return values with exceptions thrown
* Extention package adds support for Scala's `Either` and Cats' `IO`
* Extensive error reporting when parsing font files
* Builder used to avoid to deal directly with each and every option
* Scaladoc for all the API
* Support for Scala 2.13.x and 2.12.x
