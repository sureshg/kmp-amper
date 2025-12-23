package dev.suresh.ffm

import java.lang.foreign.AddressLayout
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import sun.misc.Unsafe

val LINKER: Linker = Linker.nativeLinker()

/** Symbols loaded via caller's class loader (System.loadLibrary) if found, else from libc */
val SYMBOLS: SymbolLookup by lazy { SymbolLookup.loaderLookup().or(LINKER.defaultLookup()) }

val C_CHAR = LINKER.canonicalLayouts()["char"] as ValueLayout.OfByte

val C_INT = LINKER.canonicalLayouts()["int"] as ValueLayout.OfInt

val C_LONG = LINKER.canonicalLayouts()["long"] as ValueLayout.OfLong

val C_POINTER: AddressLayout =
    (LINKER.canonicalLayouts()["void*"] as AddressLayout).withTargetLayout(
        MemoryLayout.sequenceLayout(Long.MAX_VALUE, C_CHAR)
    )

val UNSAFE by lazy {
  Unsafe::class.java.getDeclaredField("theUnsafe").run {
    isAccessible = true
    get(null) as Unsafe
  }
}

fun downcallHandle(
    symbol: String,
    fdesc: FunctionDescriptor,
    vararg options: Linker.Option,
): MethodHandle? = SYMBOLS.findOrThrow(symbol).let { LINKER.downcallHandle(it, fdesc, *options) }
