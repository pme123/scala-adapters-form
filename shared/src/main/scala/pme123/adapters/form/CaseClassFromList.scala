package pme123.adapters.form

object CaseClassFromList extends App {

  sealed trait HList

  final case class HCons[H, T <: HList](head : H, tail : T) extends HList {
    def ::[H1](h : H1) = HCons(h, this)
    override def toString = head+" :: "+tail.toString
  }

  trait HNil extends HList {
    def ::[H1](h : H1) = HCons(h, this)
    override def toString = "HNil"
  }

  case object HNil extends HNil
  type ::[H, T <: HList] = HCons[H, T]


  trait FoldCurry[L <: HList, F, Out] {
    def apply(l : L, f : F) : Out
  }

  // Base case for HLists of length one
  implicit def foldCurry1[H, Out] = new FoldCurry[H :: HNil, H => Out, Out] {
    def apply(l : H :: HNil, f : H => Out) = f(l.head)
  }

  // Case for HLists of length n+1
  implicit def foldCurry2[H, T <: HList, FT, Out]
  (implicit fct : FoldCurry[T, FT, Out]) = new FoldCurry[H :: T, H => FT, Out] {
    def apply(l : H :: T, f : H => FT) = fct(l.tail, f(l.head))
  }

  // Public interface ... implemented in terms of type class and instances above
  def foldCurry[L <: HList, F, Out](l : L, f : F)
                                   (implicit fc : FoldCurry[L, F, Out]) : Out = fc(l, f)


  case class A(x: String, y: String, z: String)

  //val l = List("a", "b", "c")
  val lh = "a" :: "b" :: "c" :: HNil

  val newA = foldCurry(lh, A.curried)

  override def main(args: Array[String]): Unit = {
    println(newA)
  }

}