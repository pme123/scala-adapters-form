package pme123.adapters.form

import pme123.adapters.form.MapEncoder.pureObject
import shapeless.LabelledGeneric.Aux
import shapeless.labelled.FieldType
import shapeless.ops.hlist.Align
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import shapeless.{::, LabelledGeneric, ops, syntax}

import scala.collection.immutable

trait MapDecoder[A] {
  def decode(value: MapValue): A
}

trait MapObjectDecoder[A] extends MapDecoder[A] {
}

object MapDecoder {

  def apply[A](implicit enc: MapDecoder[A]): MapDecoder[A] = enc

  // SAM does not work here!

  //noinspection ConvertExpressionToSAM
  def pure[A](func: MapValue => A): MapDecoder[A] =
    new MapDecoder[A] {
      def decode(value: MapValue): A = {
        func(value)
      }
    }

  // SAM does not work here!

  //noinspection ConvertExpressionToSAM
  def pureObject[A](func: MapObject => A): MapObjectDecoder[A] =
    new MapObjectDecoder[A] {
      def decode(value: MapValue): A = value match {
        case mo: MapObject => func(mo)
        case other => throw DecoderException(s"This requires a MapObject, not $other")
      }
    }

  // primitive instances
  implicit val stringDecoder: MapDecoder[String] =
    pure(mv => mv.value.asInstanceOf[String])
  implicit val doubleDecoder: MapDecoder[Double] =
    pure(mv => mv.value.asInstanceOf[Double])
  implicit val intDecoder: MapDecoder[Int] =
    pure(mv => mv.value.asInstanceOf[Int])
  implicit val booleanDecoder: MapDecoder[Boolean] =
    pure(mv => mv.value.asInstanceOf[Boolean])

  // few instance combinators
  implicit def listDecoder[A](implicit enc: MapDecoder[A]): MapDecoder[List[A]] =
    pure(mv => mv.values)

  implicit def optionDecoder[A](implicit enc: MapDecoder[A]): MapDecoder[Option[A]] =
    pure {
      case MapNull => None
      case other: MapValue => Some(enc.decode(other))
    }


  implicit val hnilDecoder: MapObjectDecoder[HNil] =
    pureObject(_ => HNil)

    implicit def hlistObjectDecoder[K <: Symbol, H, T <: HList](
                                                                   implicit
                                                                   witness: Witness.Aux[K]
                                                                   , hDecoder: Lazy[MapDecoder[H]]
                                                                   , tDecoder: MapObjectDecoder[T]
                                                                 ): MapObjectDecoder[FieldType[K, H] :: T] = {
        val fieldName = witness.value.name
        pureObject {
          case mo: MapObject =>

           val fields: immutable.Seq[(Symbol, MapValue)] = mo.fields.toList

          case other => throw DecoderException(s"This requires a MapObject, not $other")
        }
      }

  /*
  implicit def hlistObjectDecoder[K <: Symbol, H, T <: HList](
                                                               implicit
                                                               witness: Witness.Aux[K]
                                                               , hDecoder: Lazy[MapDecoder[H]]
                                                               , tDecoder: MapObjectDecoder[T]
                                                             ): MapObjectDecoder[FieldType[K, H] :: T] = {
    import syntax.singleton._

    val fieldName = witness.value.name
    pureObject {
      case mapObject: MapObject =>
        val list: immutable.List[(ObjectKey, MapValue)] = mapObject.fields.toList

            val head = hDecoder.value.decode(genericObjectDecoder(list.head))
            val fields = mapObject.fields.toList
            val tail = tDecoder.decode(MapObject(fields.tail.toMap))

            throw DecoderException(s"The field list should not be empty")

      case other => throw DecoderException(s"This requires a MapObject, not $other")
    }
  }
*/
  implicit def genericObjectDecoder[A, H <: HList](
                                                    implicit
                                                    generic: LabelledGeneric.Aux[A, H],
                                                    hDecoder: Lazy[MapObjectDecoder[H]]
                                                  ): MapDecoder[A] =
    pureObject { value =>
      generic.from(hDecoder.value.decode(value))
    }


}
