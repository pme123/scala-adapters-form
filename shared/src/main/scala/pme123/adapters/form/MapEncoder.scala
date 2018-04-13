package pme123.adapters.form

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
    pure(str => MapString(str))
  implicit val doubleEncoder: MapEncoder[Double] =
    pure(num => MapDouble(num))
  implicit val intEncoder: MapEncoder[Int] =
    pure(num => MapInt(num))
  implicit val booleanEncoder: MapEncoder[Boolean] =
    pure(bool => MapBoolean(bool))

  // few instance combinators
  implicit def listEncoder[A](implicit enc: MapEncoder[A]): MapEncoder[List[A]] =
    pure(list => MapList(list.map(enc.encode)))

  implicit def optionEncoder[A](implicit enc: MapEncoder[A]): MapEncoder[Option[A]] =
    pure(opt => opt.map(enc.encode).getOrElse(MapNull))


  implicit val hnilEncoder: MapObjectEncoder[HNil] =
    pureObject(hnil => MapObject(Nil))

  import shapeless.labelled.FieldType

  implicit def hlistObjectEncoder[K <: Symbol, H, T <: HList](
                                                               implicit
                                                               witness: Witness.Aux[K]
                                                               , hEncoder: Lazy[MapEncoder[H]]
                                                               , tEncoder: MapObjectEncoder[T]
                                                             ): MapObjectEncoder[FieldType[K, H] :: T] = {
    val fieldName = witness.value.name
    pureObject { hlist =>
      val head = hEncoder.value.encode(hlist.head)
      val tail = tEncoder.encode(hlist.tail)
      MapObject((fieldName, head) :: tail.fields)
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
