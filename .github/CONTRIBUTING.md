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

## Project structure

Figlet4s is a multi-module sbt project. The root project aggregates the library modules; the microsite is a standalone sbt 1.x project.

| Module              | Directory              | Description                                                                                                                                 |
|---------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `figlet4s` (root)   | `.`                    | Aggregates `figlet4sCore`, `figlet4sEffects` and `figlet4sJava`; builds the unified Scala API documentation (`sbt unidoc`).                   |
| `figlet4sCore`      | `figlet4s-core/`       | The core library: FIGfont parsing, FIGure rendering, render options and the builder. It bundles 450+ FIGfonts under `src/main/resources/fonts`. Cross-built for Scala 3 and Scala 2.13. |
| `figlet4sEffects`   | `figlet4s-effects/`    | Effectful APIs on top of the core: package `catsio` (Cats Effect `IO`) and package `either` (`Either`). It depends on `figlet4sCore` (main, test and integration-test sources). Cross-built for Scala 3 and Scala 2.13. |
| `figlet4sJava`      | `figlet4s-java/`       | Java-friendly API (`com.colofabrix.java.figlet4s`). It depends on `figlet4sCore`. Built for a single Scala version and excluded from the unified API documentation. |
| `figlet4sMicrosite` | `figlet4s-microsite/`  | Documentation website (sbt-microsites + Jekyll). It is a standalone sbt 1.x build inside `figlet4s-microsite/`: run `sbt makeMicrosite` there and open `target/site`. It needs Ruby and Jekyll locally, and its snippets are mdoc-compiled against the latest released version of the library.                                       |

Each library module follows the standard sbt layout:

* `src/main` — library code
* `src/test` — unit tests

The integration tests live in a dedicated project, `figlet4sIt` in `figlet4s-it/`, using the standard
`src/test` layout and aggregated by the root project, so plain `sbt test` runs them together with
the unit tests.

## Requirements

* JDK 17 — the version used by CI.
* sbt — the version used is pinned in [`project/build.properties`](../project/build.properties).
* `figlet` on the PATH — required only by the figlet-parity integration tests (see
  [The figlet-parity suite](#the-figlet-parity-suite)). Everything else runs without it: when
  `figlet` is missing, the parity tests cancel themselves and the rest of the build is unaffected.

## Building and testing

All commands are run from the repository root. The project convention is:

```
sbt --client --color=false --supershell=false --batch "<command>"
```

Plain `sbt <command>` behaves the same way.

### Whole build

| Command                       | What it does                                                                    |
|-------------------------------|---------------------------------------------------------------------------------|
| `sbt test` | All tests, unit and integration, for the current Scala version |
| `sbt +testFull` | All tests, unit and integration, for all supported Scala versions (3 and 2.13) |
| `sbt figlet4sIt/test` | Integration tests only, for the current Scala version |
| `sbt styleCheck`              | Checks formatting (scalafmt) and code style (scalafix) without modifying files    |
| `sbt styleApply`              | Formats and fixes the code in place                                               |
| `sbt unidoc`                  | Builds the unified Scala API documentation                                        |
| `sbt makeMicrosite` (inside `figlet4s-microsite/`, sbt 1.x) | Builds the documentation microsite (Jekyll; needs Ruby + Jekyll locally) |

### Single modules

| Command                                  | What it does                                                                     |
|------------------------------------------|----------------------------------------------------------------------------------|
| `sbt figlet4sCore/test`                  | Unit tests of the core module (current Scala version)                             |
| `sbt figlet4sEffects/test`               | Unit tests of the effects module (current Scala version)                          |
| `sbt figlet4sJava/test`                  | Tests of the Java API module                                                      |
| `sbt figlet4sIt/testOnly com.colofabrix.scala.figlet4s.unsafe.UnsafeFiglet4sSpecs` | Only the figlet-parity suite |

Prefix any of them with `+` to cross-build over all supported Scala versions, for example
`sbt +figlet4sCore/test`.

### Running a single suite

```
sbt "figlet4sCore/testOnly com.colofabrix.scala.figlet4s.figfont.FIGfontSpecs"
```

## The figlet-parity suite

The most important integration tests are the **figlet-parity** tests: they verify that Figlet4s
renders text exactly like the original `figlet` command-line program.

* Suite:
  [`UnsafeFiglet4sSpecs`](../figlet4s-it/src/test/scala/com/colofabrix/scala/figlet4s/unsafe/UnsafeFiglet4sSpecs.scala)
  — the test "render the texts as the original command line FIGlet does", tagged `SlowTest`.
* Support code:
  [`OriginalFigletTesting.scala`](../figlet4s-it/src/test/scala/com/colofabrix/scala/figlet4s/testutils/OriginalFigletTesting.scala)

How it works:

1. It enumerates all bundled fonts (minus a short list of fonts that misbehave with the original
   figlet, too), all horizontal layouts and all justifications.
2. For each combination it generates 50 random strings of printable ASCII characters.
3. Each string is rendered both by Figlet4s and by the `figlet` executable, invoked with the same
   font file and the same options, and the two outputs are compared ignoring trailing whitespace.

### Installing figlet

The parity tests need the original `figlet` executable on the PATH:

* Ubuntu/Debian: `sudo apt-get install figlet` (this is what CI installs)
* macOS: `brew install figlet`
* Windows: install it inside [WSL](https://learn.microsoft.com/windows/wsl/) or use any other source
  of a `figlet` binary on the PATH.

CI always has figlet installed, so the parity tests run there on every build.

### Running the parity tests

```
sbt figlet4sIt/test
```

The parity test is tagged `SlowTest` because it takes several minutes. To run only that test:

```
sbt "figlet4sIt/testOnly com.colofabrix.scala.figlet4s.unsafe.UnsafeFiglet4sSpecs -- -n SlowTest"
```

Two tags are available in
[`Figlet4sTags`](../figlet4s-it/src/test/scala/com/colofabrix/scala/figlet4s/testutils/Figlet4sTags.scala)
to mark long-running or manually-run tests: `SlowTest` and `ManualRunTest`.

## Pull requests

### First run

To start contributing you need to:

* have [SBT installed](https://www.scala-sbt.org/) on your machine;
* clone this repository with `git clone`;
* use your favourite editor (we love [VSCode + Metals](https://scalameta.org/metals/docs/editors/vscode.html));
* use GIT to manage your changes.

### Issues and Branches

We like to keep things tidy and we ask you to create an Issue for each change you want to submit and
to put your changes on a GIT branch.

One issue, one branch.

### Tests and Code style

Each Pull Request is validated by an automatic tool that checks several aspects of the code:

* All tests must pass for all the supported versions of Scala (`sbt +testFull`), including the
  figlet-parity suite
* Documentation must be present and build successfully (`sbt unidoc`)
* Code must be properly formatted (`sbt styleApply`)

All these checks must pass for the PR to be accepted.

If you write any code, be sure to write the tests that go with it and, before pushing anything, be sure to check that the tests are OK by running `sbt +testFull`. The
figlet-parity tests require that you have figlet installed on your system and available on the PATH
(see [The figlet-parity suite](#the-figlet-parity-suite)); without it they are cancelled, not failed.

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
