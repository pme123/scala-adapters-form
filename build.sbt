import sbtcrossproject.{CrossType, crossProject}
import Settings._

lazy val adaptersForm = project.in(file(".")).
  aggregate(sharedJvm, sharedJs)
  .settings(organizationSettings)
  .settings(
    publish := {}
    , publishLocal := {}
    , publishArtifact := false
    , isSnapshot := true
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings())
  .settings(sharedDependencies)
  .jsSettings(jsSettings)
  .jsSettings(sharedJsDependencies) // defined in sbt-scalajs-crossproject
  .jvmSettings(/* ... */)
  .jsConfigure(_ enablePlugins ScalaJSPlugin)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
