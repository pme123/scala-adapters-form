package pme123.adapters.form

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.immutable.Seq
import scala.meta._

/** example usages: see MappableTest.scala */
@compileTimeOnly("@scala.meta.serialiser.bindable not expanded")
class bindable(annotationParams: Map[String, Any] = Map.empty) extends StaticAnnotation {
   inline def apply(defn: Any): Any = meta {

    // defined class may or may not have a companion object
    val (classDefn: Defn.Class, compDefn: Defn.Object) = defn match {
      case classDefn: Defn.Class => (classDefn, q"object ${Term.Name(classDefn.name.value)}") // only class + new companion
      case Term.Block((classDefn: Defn.Class) :: (compDefn: Defn.Object) :: Nil) => (classDefn, compDefn) // class + companion
      case _ => abort("Invalid annottee - you can only use @bindable on case classes")
    }

    // get existing companion object statements (if any)
    val compStats: Seq[Stat] =compDefn.templ.stats.getOrElse(Nil)

    val q"..$mods class $tName[..$tParams] ..$ctorMods (...$paramss) extends $template" = classDefn
    val typeTermName: Term.Name = Term.Name(tName.value)

    val formDataName = tName.copy(value = s"${tName.value}FormData")

    val ctorArgs = paramss.map { params =>
    params.map{ param =>
      param.decltpe.get match{

      case argTyp: Type.Arg if argTyp.toString().startsWith("Seq[") =>

        val newType = s"com.thoughtworks.binding.Binding.Vars[ ${argTyp.children.last} ]".parse[Type].get
        println(s"old: ${argTyp}")
        println(s"argTyp.children: ${argTyp.children}")
        println(s"newType Seq: $newType")
        param.copy(decltpe = Some(newType))

      case argTyp =>
        println(s"oldType: $argTyp ")
        val newType = s"com.thoughtworks.binding.Binding.Var[ $argTyp ]".parse[Type].get
        println(s"newType: $newType")
        param.copy(decltpe = Some(newType))
    }}}

    val res =
      q"""

      ..$mods class $tName[..$tParams](...$paramss) extends $template

      object $typeTermName {



        ..$compStats
      }

      case class $formDataName[..$tParams] ..$ctorMods (...$ctorArgs)

    """
    println (res)
    res
  }
}
