package dev.suresh.ffm

import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import sun.misc.Unsafe

/** Linker for the current platform's native calling conventions (ABI). */
val LINKER: Linker = Linker.nativeLinker()

/**
 * Symbol lookup for native functions.
 * - Windows: kernel32 + advapi32
 * - POSIX: System.loadLibrary + libc
 */
val SYMBOLS: SymbolLookup by lazy {
  val isWin = System.getProperty("os.name").lowercase().startsWith("win")
  when (isWin) {
    true ->
        SymbolLookup.libraryLookup("kernel32", Arena.global())
            .or(SymbolLookup.libraryLookup("advapi32", Arena.global()))
    else -> SymbolLookup.loaderLookup().or(LINKER.defaultLookup())
  }
}

val C_CHAR = LINKER.canonicalLayouts()["char"] as ValueLayout.OfByte

val C_INT = LINKER.canonicalLayouts()["int"] as ValueLayout.OfInt

val C_LONG = LINKER.canonicalLayouts()["long"] as ValueLayout.OfLong

val C_POINTER: AddressLayout =
    (LINKER.canonicalLayouts()["void*"] as AddressLayout).withTargetLayout(
        MemoryLayout.sequenceLayout(Long.MAX_VALUE, C_CHAR)
    )

val WCHAR: ValueLayout.OfChar = ValueLayout.JAVA_CHAR

val UNSAFE by lazy {
  Unsafe::class.java.getDeclaredField("theUnsafe").run {
    isAccessible = true
    get(null) as Unsafe
  }
}

/** Creates a method handle for calling the native [symbol] with the given [fdesc] signature. */
fun downcallHandle(
    symbol: String,
    fdesc: FunctionDescriptor,
    vararg options: Linker.Option,
): MethodHandle = LINKER.downcallHandle(SYMBOLS.findOrThrow(symbol), fdesc, *options)
