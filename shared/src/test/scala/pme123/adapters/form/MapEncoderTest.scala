package pme123.adapters.form

class MapEncoderTest extends UnitTest {

  case class IceCream(name: String, numCherries: Int, inCone: Boolean, flavours: List[String])


  "MapEncoder" should "create a Map from an IceCream" in {
    val iceCream = IceCream("hello", 12, inCone = true, List("sweet", "strawberry"))

    val mapValue = MapEncoder[IceCream].encode(iceCream)

    mapValue.varValue[String]('name).value shouldBe "hello"
    mapValue.varValue[Int]('numCherries).value shouldBe 12
    mapValue.varValue[Boolean]('inCone).value shouldBe true
    mapValue.varsValue[String]('flavours).value shouldBe Seq("sweet", "strawberry")


  }

}
