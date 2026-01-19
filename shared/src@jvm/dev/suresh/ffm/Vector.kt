@file:Suppress("UNCHECKED_CAST")

package dev.suresh.ffm

import jdk.incubator.vector.*

// Generic Vector-to-Vector Operations

operator fun <E, V : Vector<E>> V.plus(other: V): V = this.add(other) as V

operator fun <E, V : Vector<E>> V.minus(other: V): V = this.sub(other) as V

operator fun <E, V : Vector<E>> V.times(other: V): V = this.mul(other) as V

operator fun <E, V : Vector<E>> V.div(other: V): V = this.div(other) as V

operator fun <E, V : Vector<E>> V.unaryMinus(): V = this.neg() as V

operator fun <E, V : Vector<E>> V.unaryPlus(): V = this

// Index Access

operator fun FloatVector.get(index: Int): Float = this.lane(index)

operator fun IntVector.get(index: Int): Int = this.lane(index)

operator fun LongVector.get(index: Int): Long = this.lane(index)

operator fun DoubleVector.get(index: Int): Double = this.lane(index)

operator fun ByteVector.get(index: Int): Byte = this.lane(index)

operator fun ShortVector.get(index: Int): Short = this.lane(index)
