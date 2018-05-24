package pme123.adapters.form

import com.thoughtworks.binding.Binding.{Var, Vars}
import shapeless.{HList, HNil, Lazy, Witness, _}

trait MapEncoder[A] {
  def encode(value: A): MapValue
}

trait MapObjectEncoder[A] extends MapEncoder[A] {
  def encode(value: A): MapObject
}

object MapEncoder {

  def apply[A](implicit enc: MapEncoder[A]): MapEncoder[A] = enc

  // SAM does not work here!

  //noinspection ConvertExpressionToSAM
  def pure[A](func: A => MapValue): MapEncoder[A] =
    new MapEncoder[A] {
      def encode(value: A): MapValue =
        func(value)
    }

  // SAM does not work here!

  //noinspection ConvertExpressionToSAM
  def pureObject[A](func: A => MapObject): MapObjectEncoder[A] =
    new MapObjectEncoder[A] {
      def encode(value: A): MapObject =
        func(value)
    }

  // primitive instances
  implicit val stringEncoder: MapEncoder[String] =
    pure(str => MapString(Var(str)))
  implicit val doubleEncoder: MapEncoder[Double] =
    pure(num => MapDouble(Var(num)))
  implicit val intEncoder: MapEncoder[Int] =
    pure(num => MapInt(Var(num)))
  implicit val booleanEncoder: MapEncoder[Boolean] =
    pure(bool => MapBoolean(Var(bool)))

  // few instance combinators
  implicit def listEncoder[A](implicit enc: MapEncoder[A]): MapEncoder[List[A]] =
    pure(list => MapList(Vars(list: _*)))

  implicit def optionEncoder[A](implicit enc: MapEncoder[A]): MapEncoder[Option[A]] =
    pure(opt => opt.map(enc.encode).getOrElse(MapNull))


  implicit val hnilEncoder: MapObjectEncoder[HNil] =
    pureObject(_ => MapObject(Map()))

  import shapeless.labelled.FieldType

  implicit def hlistObjectEncoder[K <: Symbol, H, T <: HList](
                                                               implicit
                                                               witness: Witness.Aux[K]
                                                               , hEncoder: Lazy[MapEncoder[H]]
                                                               , tEncoder: MapObjectEncoder[T]
                                                             ): MapObjectEncoder[FieldType[K, H] :: T] = {
    val fieldName = witness.value
    pureObject { hlist =>
      val head: MapValue = hEncoder.value.encode(hlist.head)
      val tail: MapObject = tEncoder.encode(hlist.tail)
      MapObject(tail.fields + (fieldName -> head))
    }
  }

  implicit def genericObjectEncoder[A, H <: HList](
                                                    implicit
                                                    generic: LabelledGeneric.Aux[A, H],
                                                    hEncoder: Lazy[MapObjectEncoder[H]]
                                                  ): MapEncoder[A] =
    pureObject { value =>
      hEncoder.value.encode(generic.to(value))
    }


}
