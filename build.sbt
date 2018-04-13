
scalacOptions in Global ++= Seq(
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

libraryDependencies in Global ++= Seq(
  "com.chuusai" %%% "shapeless" % "2.3.3"
  , "com.thoughtworks.binding" %%% "dom" % "11.0.1"
  , "org.scalameta" %%% "scalameta" % "1.8.0"
  , "org.typelevel" %%% "cats-core" % "1.0.1"
  // testing
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
)


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "pme123",
      scalaVersion := "2.12.4",
      version := "0.1.0"
    )),
    name := "scala-adapters-form"
    , addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
  ).enablePlugins(ScalaJSPlugin)

