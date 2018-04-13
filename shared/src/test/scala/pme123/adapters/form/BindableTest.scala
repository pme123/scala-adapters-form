package pme123.adapters.form

import com.thoughtworks.binding.Binding._
import org.scalatest._

class BindableTest extends WordSpec with Matchers {

  @bindable
  case class SimpleCaseClass(i: Int, s: String, list: Seq[String])

  object SimpleCaseClass {
    val hello = "existing object"
  }

  "simple case class" should {
    "serialise and deserialise" in {
     // val testInstance = SimpleCaseClass(i = 42, s = "something")
      """SimpleCaseClassFormData(Var(55), Var("other"), Vars("elem1"))""" should compile
    }
  }

}
