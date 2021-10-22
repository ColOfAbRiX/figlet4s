# Contributing

We are very happy to welcome new contributors!

## How can I contribute?

### Report a bug

If you think you have found a bug, please search our issue tracker to see if anyone has already
reported it.

If you are the first to have noticed it, please create an issue and make sure to provide any
information that might help us resolve it. The "how to reproduce" is particularly important as it is
the only way we have to see and catch the bug.

You are welcome to try and fix your bug by submitting a pull request, if you would like to (see Pull
Requests section for more information).

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

One issue, one branch.

### Tests and Code style

Each Pull Request is validated by an automatic tool that checks several aspects of the code:

* All test must pass for all the supported versions of Scala (`sbt +test`)
* Documentation must be present and build successfully (`sbt unidoc`)
* Code must be properly formatted (`sbt styleApply`)

All these test must pass for the PR to be accepted.

If you write any code, be sure to write the tests that go with it and, before pushing anything, be
sure to check that the tests are OK by running `sbt +test`. Some tests require that you have figlet
installed on your system and available on the PATH.

Code styling is checked by two tools that run in automatic: scalafmt and scalafix. To run them both
before pushing you can use `sbt styleApply`.

The API documentation is also an important part of Figlet4s because it helps developers understand
how to use the library so it's important that you write it and that it can be included in the
release. You can test and generate the documentation by using `sbt unidoc` on the root project.

### Documentation

The better the documentation, the fewer things users will have to wonder about.

Feel free do add more documentation, to make corrections and to clarify parts that you think are not
clear enough.

## Code of conduct

In order to keep a happy and respectful atmosphere around the project, we expect everyone
participating in it to follow our [Code of conduct](CODE_OF_CONDUCT.md).
