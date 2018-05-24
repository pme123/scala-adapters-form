package pme123.adapters.form

import com.thoughtworks.binding.Binding.{Var, Vars}
import shapeless.{::, LabelledGeneric, ops, syntax}

class MapDecoderTest extends UnitTest {

  case class IceCream(name: String, numCherries: Int, inCone: Boolean, flavours: List[String])

  "MapDecoder" should "create a Case Class from an HList" in {
    val iceCream = IceCream("hello", 12, inCone = true, List("sweet", "strawberry"))
    val mapString = MapString(Var("hello"))
    val mapInt = MapInt(Var(12))
    val mapDouble = MapDouble(Var(3.3))
    val mapBoolean = MapBoolean(Var(false))
    val mapList = MapList(Vars("sweet", "strawberry"))

    assert(MapDecoder[Int].decode(mapInt) == 12)
    assert(MapDecoder[Double].decode(mapDouble) == 3.3)
    assert(!MapDecoder[Boolean].decode(mapBoolean))
    assert(MapDecoder[String].decode(mapString) == "hello")
    assert(MapDecoder[List[String]].decode(mapList) == List("sweet", "strawberry"))

//    val newIceCream = MapDecoder[IceCream].decode(MapEncoder[IceCream].encode(iceCream))
 //   assert(newIceCream == iceCream)
  }

  "LabelledGenerics" should "encode decode out of the box" in {
    val iceCream = IceCream("hello", 12, inCone = true, List("sweet", "strawberry"))
    // val mapString = MapString(Var("hello"))
    // val mapObject = MapObject(Map("name" -> mapString))

    // val mIceCream = MapDecoder[IceCream].decode(mapObject)

    // assert(mIceCream == iceCream)
    val iceCreamGen = LabelledGeneric[IceCream]
    val iGen = iceCreamGen.to(iceCream)

    assert(iceCreamGen.from(iGen) == iceCream)
  }

  "The labelled example" should "create a Case Class from an HList" in {
    case class A(x: String, y: String, z: Int, a: List[String])
    import ops.hlist.Align
    import syntax.singleton._

    case class From(s1: String, s2: String)
    case class To(s2: String, i: Int, s1: String)

    val from = From("foo", "bar")

    val fromGen = LabelledGeneric[From]
    val toGen = LabelledGeneric[To]

    // Define the type of the i field by example
    val iField = Field('i ->> 0)

    val align = Align[iField.F :: fromGen.Repr, toGen.Repr]

    val to = toGen.from(align('i ->> 23 :: fromGen.to(from)))
    println(to)
    println

  }


}
