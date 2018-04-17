import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.Stage
import sbt.Keys.{resolvers, scalacOptions, _}
import sbt.{Def, URL, _}

object Settings {

  lazy val orgId = "pme123"
  lazy val orgHomepage = Some(new URL("https://github.com/pme123"))
  lazy val projectName = "scala-adapters-form"
  lazy val projectV = "0.1.1"

  // main versions
  lazy val scalaV = "2.12.5"
  lazy val bindingV = "11.0.1"

  lazy val scalaTestV = "3.0.4"

  lazy val buildVersion: String = sys.env.getOrElse("BUILD_VERSION", default = projectV)
  lazy val buildNumber: String = sys.env.getOrElse("BUILD_NUMBER", default = s"${(System.currentTimeMillis / 1000).asInstanceOf[Int]}")

  lazy val organizationSettings = Seq(
    organization := orgId
    , organizationHomepage := orgHomepage
  )

  lazy val testStage: Stage = sys.props.get("testOpt").map {
    case "full" => FullOptStage
    case "fast" => FastOptStage
  }.getOrElse(FastOptStage)

  lazy val sharedDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
    "com.chuusai" %%% "shapeless" % "2.3.3"
    , "com.thoughtworks.binding" %%% "binding" % "11.0.1"
    , "org.typelevel" %%% "cats-core" % "1.0.1"
    , "org.scalatest" %%% "scalatest" % scalaTestV % Test

  ))

  lazy val jsSettings: Seq[Def.Setting[_]] = Seq(
    scalaJSStage in Global := testStage
  )

  lazy val sharedJsDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
  ))

  def sharedSettings(moduleName: Option[String] = None): Seq[Def.Setting[_]] = Seq(
    scalaVersion := scalaV
    , name := s"$projectName${moduleName.map("-" + _).getOrElse("")}"
    , description := "scala-adapters showcase without a JobProcess."
    , version := s"$buildVersion-$buildNumber"
    , resolvers += "jitpack" at "https://jitpack.io"
    , publishArtifact in(Compile, packageDoc) := false
    , publishArtifact in packageDoc := false
    , sources in(Compile, doc) := Seq.empty
    , scalacOptions ++= Seq("-Xmax-classfile-name", "78")
    , addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    // cats
    , scalacOptions += "-Ypartial-unification"
  ) ++ organizationSettings


}
