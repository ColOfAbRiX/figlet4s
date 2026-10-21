// Figlet4s documentation microsite.
//
// This is a standalone sbt 1.x build, independent from the main library build in the repository
// root (which uses sbt 2.x). Building the site requires Ruby and Jekyll on the system.
//
// The documentation snippets are compiled by mdoc against the latest released version of the
// library, not against the development one. Bump DocsVersion at every release.

lazy val DocsVersion = "0.3.2"

lazy val copyApiDocsToSite = taskKey[Unit]("Copies the staged API docs into the built site")

lazy val figlet4sMicrosite = project
  .in(file("."))
  .enablePlugins(MicrositesPlugin)
  .settings(
    scalaVersion                 := "3.10.0",
    organization                 := "com.colofabrix.scala",
    libraryDependencies += "com.colofabrix.scala" %% "figlet4s-effects" % DocsVersion,
    micrositeAuthor              := "ColOfAbRiX",
    micrositeBaseUrl             := "figlet4s",
    micrositeDescription         := "ASCII-art banners in Scala",
    micrositeDocumentationUrl    := "docs/",
    micrositeGithubOwner         := "ColOfAbRiX",
    micrositeGithubRepo          := "figlet4s",
    micrositeGithubToken         := sys.env.get("GITHUB_TOKEN"),
    micrositeGitterChannel       := false,
    micrositeHighlightLanguages ++= Seq("xml", "plaintext"),
    micrositeHighlightTheme      := "atom-one-dark", // https://highlightjs.org/static/demo/
    micrositeHomepage            := "https://colofabrix.github.io/",
    micrositeName                := "Figlet4s",
    micrositePushSiteWith        := GitHub4s,
    name                         := "figlet4s-microsite",
    publish / skip               := true,
    // Host the API documentation on GitHub Pages: Scala unidoc under /api, Java javadoc under /api-java.
    // The docs are staged by the main build (sbt copyApiDocsToSite, run from the repository root).
    copyApiDocsToSite := {
      val stagingDir = baseDirectory.value / "api-docs"
      val siteDir    = (makeSite / target).value
      IO.copyDirectory(stagingDir / "api", siteDir / "api", overwrite = true)
      IO.copyDirectory(stagingDir / "api-java", siteDir / "api-java", overwrite = true)
    },
    publishMicrosite := Def.sequential(clean, makeMicrosite, copyApiDocsToSite, pushMicrosite).value,
    mdocVariables := Map(
      "VERSION"       -> DocsVersion,
      "SCALA_VERSION" -> """\d+\.\d+""".r.findFirstIn(scalaVersion.value).getOrElse(""),
    ),
  )
