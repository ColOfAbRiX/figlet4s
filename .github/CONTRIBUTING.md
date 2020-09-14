# Contributing

We are very happy to welcome new contributors!

## How can I contribute?

### Report a bug

If you think you have found a bug, please search our issue tracker to see if anyone has already
reported it.

If you are the first to have noticed it, please create an issue, and make sure to provide any
information that might help us resolve it.

You are welcome to try and fix it by submitting a pull request if you would like to (see Pull
requests section for more information).

### Feature requests and enhancements

We are open to feature requests, be sure to search our issue tracker to see if anyone has already
asked for it.

If not, please create an issue describing what you want, what your use case is, and an example of
code.

You are welcome to try and do it yourself by submitting a pull request if you would like to (see
Pull requests section for more information).

If you think of anything that might help us improve the project, please create an issue and we will
be happy to discuss it with you.

## Pull requests

### First run

To start contributing you need to:

* have [SBT installed](https://www.scala-sbt.org/1.x/docs/Setup.html) on your machine;
* clone this repository with `git clone`;
* use your favourite editor (we love [VSCode + Metals](https://scalameta.org/metals/docs/editors/vscode.html));
* use GIT to manage your changes.

### Issues and Branches

We like to keep things tidy and we ask you to create an Issue for each change you want to submit and
to put your changes on a GIT branch.

### Tests and Code style

If you write any code, be sure to write the test that goes with it and before pushing anything,
please be sure to check that the tests are OK by running `sbt test`. Some tests require that you
have figlet installed on your system and available on the PATH.

Code styling is ensured by two tools that run in automatic: scalafmt and scalafix. You don't have
to do anything in particular because the tools will format the code when the code is compiled.

### Documentation

The better the documentation, the fewer things users will have to wonder about.

Feel free do add more documentation, to make corrections and to clarify parts that you think are not
clear enough.

## Code of conduct

In order to keep a happy and respectful atmosphere around the project, we expect everyone
participating in it to follow our [Code of conduct](CODE_OF_CONDUCT.md).
