import org.scalajs.sbtplugin.Stage
import sbtcrossproject.{CrossType, crossProject}

val scalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-Xlint",
  // "-Xfatal-warnings",
  "-Ywarn-dead-code"
  , "-Xplugin-require:macroparadise"
  , "-language:existentials"
)

val sharedDependencies = Def.settings(libraryDependencies ++= Seq(
  "com.chuusai" %%% "shapeless" % "2.3.3"
  , "com.thoughtworks.binding" %%% "binding" % "11.0.1"
  , "org.scalameta" %%% "scalameta" % "1.8.0"
  , "org.typelevel" %%% "cats-core" % "1.0.1"
  // testing
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
))

lazy val testStage: Stage = sys.props.get("testOpt").map {
  case "full" => FullOptStage
  case "fast" => FastOptStage
}.getOrElse(FastOptStage)

lazy val adaptersForm = project.in(file(".")).
  aggregate(sharedJvm, sharedJs)
  .settings(
    name := "scala-adapters-form"
    , organization := "pme123"
    , scalaVersion := "2.12.4"
    , version := "0.1.0"
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedDependencies)
  .settings(
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  ).jsSettings(
  scalaJSStage in Global := testStage
).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js