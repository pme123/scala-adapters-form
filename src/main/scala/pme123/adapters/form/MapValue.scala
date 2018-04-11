package pme123.adapters.form

sealed trait MapValue

case class MapObject(fields: List[(String, MapValue)]) extends MapValue

case class MapList(items: List[MapValue]) extends MapValue

case class MapString(value: String) extends MapValue

case class MapInt(value: Int) extends MapValue

case class MapDouble(value: Double) extends MapValue

case class MapBoolean(value: Boolean) extends MapValue

case object MapNull extends MapValue