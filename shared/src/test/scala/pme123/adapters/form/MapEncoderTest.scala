package pme123.adapters.form

class MapEncoderTest extends UnitTest {

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)


  "MapEncoder" should "create a Map from an IceCream" in {
    val iceCream = IceCream("hello", 12, inCone = true)

    val mapValue = MapEncoder[IceCream].encode(iceCream)

    mapValue.valueAsVar[String]("name").value shouldBe "hello"

    mapValue shouldBe MapObject(List(
        ("name", MapString("hello"))
        , ("numCherries", MapInt(12))
        , ("inCone", MapBoolean(true))
      ))

  }

}
