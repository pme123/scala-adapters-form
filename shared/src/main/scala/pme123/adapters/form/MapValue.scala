package pme123.adapters.form

import com.thoughtworks.binding.Binding.{Var, Vars}

sealed trait MapValue {
  def valueAsVar[A](ident: String): Var[A] = throw new UnsupportedOperationException("valueAsVar is only supported for MapObjects")
}

case class MapObject(fields: List[(String, MapValue)]) extends MapValue {
  override def valueAsVar[A](ident: String): Var[A] =
    Var(
      fields.find(_._1 == ident)
        .map(_._2)
        .map(_.asInstanceOf[SimpleValue[A]])
        .map(_.value)
        .getOrElse(throw new IllegalArgumentException(s"There is no value for $ident"))
    )
}

case class MapList[A](items: Seq[SimpleValue[A]]) extends MapValue {
  def asVars: Vars[A] = Vars[A](items.map(_.value): _*)
}

sealed trait SimpleValue[A]
  extends MapValue {
  def value: A

  def asVar: Var[A] = Var(value)

}

case class MapString(value: String) extends SimpleValue[String] {
}

case class MapInt(value: Int) extends SimpleValue[Int] {
}

case class MapDouble(value: Double) extends SimpleValue[Double] {
}

case class MapBoolean(value: Boolean) extends SimpleValue[Boolean] {
}

case object MapNull extends MapValue