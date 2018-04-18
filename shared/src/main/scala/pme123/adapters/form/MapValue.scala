package pme123.adapters.form

import com.thoughtworks.binding.Binding.{Var, Vars}

sealed trait MapValue {
  def varValue[A](key: ObjectKey): Var[A] = this match {
    case MapObject(fields) =>
      fields.get(key) match {
        case Some(sv: SimpleValue[A]) => sv.elem
        case _ => throw MapException(s"There is no SimpleValue for $key")
      }
    case other => throw MapException(s"This needs to be a MapObject (not: $other")
  }

  def varsValue[A](key: ObjectKey): Vars[A] =this match {
    case MapObject(fields) =>
      fields.get(key) match {
        case Some(ml: MapList[A]) => ml.elems
        case _ => throw MapException(s"There is no MapList for $key")
      }
    case other => throw MapException(s"This needs to be a MapObject (not: $other")
  }
}

case class MapObject(fields: Map[ObjectKey, MapValue]) extends MapValue {

}

case class MapList[A](elems: Vars[A]) extends MapValue {

  def setValues(newValues: Seq[A]) {
    elems.value.clear()
    elems.value ++= newValues
  }

  def addValue(newValue: A) {
    elems.value += newValue
  }
}

sealed trait SimpleValue[A]
  extends MapValue {
  def elem: Var[A]

  def setValue(newValue: A) {
    elem.value = newValue
  }
}

case class MapString(elem: Var[String]) extends SimpleValue[String] {
}

case class MapInt(elem: Var[Int]) extends SimpleValue[Int] {
}

case class MapDouble(elem: Var[Double]) extends SimpleValue[Double] {
}

case class MapBoolean(elem: Var[Boolean]) extends SimpleValue[Boolean] {
}

case object MapNull extends MapValue