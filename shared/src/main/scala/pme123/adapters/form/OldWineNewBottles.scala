package pme123.adapters.form

import shapeless._
/**
  * Utility trait intended for inferring a field type from a sample value and unpacking it into its
  * key and value types.
  */
import labelled.FieldType

trait Field {
  type K
  type V
  type F = FieldType[K, V]
}

object Field {
  def apply[K0, V0](sample: FieldType[K0, V0]) = new Field { type K = K0; type V = V0 }
}
