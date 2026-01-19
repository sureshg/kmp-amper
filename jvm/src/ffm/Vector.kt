package ffm

import dev.suresh.ffm.*
import jdk.incubator.vector.*

val INT_SPECIES = IntVector.SPECIES_PREFERRED
val FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED

fun vectorApi() {
  println(INT_SPECIES)

  val a = IntArray(22)
  val b = IntArray(22)
  val c = IntArray(22)

  val loopSize = INT_SPECIES.loopBound(a.size)
  val lanes = INT_SPECIES.length()

  println("Vector Loop size: $loopSize, Number of Lanes: $lanes")

  val vc = IntVector.broadcast(INT_SPECIES, 2)
  for (i in 0..<loopSize step lanes) {
    // val mask = intSpecies.indexInRange(i, a.size)
    val va = IntVector.fromArray(INT_SPECIES, a, i)
    val vb = IntVector.fromArray(INT_SPECIES, b, i)
    val counter = i / lanes + 1
    va.add(counter).intoArray(a, i)
    vb.add(counter).intoArray(b, i)
    vc.intoArray(c, i)
  }

  println(a.contentToString())
  println(b.contentToString())
  println(c.contentToString())

  val fa = FloatArray(10) { it.toFloat() }
  val fb = FloatArray(10) { it.toFloat() }
  val s = hypo(fa, fb)
  println("Fa = ${fa.contentToString()}")
  println("Fb = ${fb.contentToString()}")
  println("Hypo = ${s.contentToString()}")
}

fun hypo(fa: FloatArray, fb: FloatArray): FloatArray {
  val lanes = FLOAT_SPECIES.length()
  val result = FloatArray(fa.size)
  for (i in fa.indices step lanes) {
    val mask = FLOAT_SPECIES.indexInRange(i, fa.size)
    val va = FloatVector.fromArray(FLOAT_SPECIES, fa, i, mask)
    val vb = FloatVector.fromArray(FLOAT_SPECIES, fb, i, mask)
    val vc = ((va * va) + (vb * vb)).sqrt()
    vc.intoArray(result, i, mask)
  }
  return result
}

fun average(arr: IntArray): Double {
  var sum = 0L // Use Long to avoid overflow
  for (i in arr.indices step INT_SPECIES.length()) {
    val mask = INT_SPECIES.indexInRange(i, arr.size)
    val v = IntVector.fromArray(INT_SPECIES, arr, i, mask)
    sum += v.reduceLanes(VectorOperators.ADD, mask)
  }
  return sum.toDouble() / arr.size
}
