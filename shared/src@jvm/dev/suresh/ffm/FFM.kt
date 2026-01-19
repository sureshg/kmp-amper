package dev.suresh.ffm

import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

/** Linker for the current platform's native calling conventions (ABI). */
val LINKER: Linker = Linker.nativeLinker()

/** Symbol lookup for native functions (kernel32/advapi32 on Windows, libc on POSIX). */
val SYMBOL_LOOKUP: SymbolLookup by lazy {
  val isWindows = System.getProperty("os.name").lowercase().startsWith("win")
  when (isWindows) {
    true ->
        SymbolLookup.libraryLookup("kernel32", Arena.global())
            .or(SymbolLookup.libraryLookup("advapi32", Arena.global()))
    else -> SymbolLookup.loaderLookup().or(LINKER.defaultLookup())
  }
}

/** C `char` type layout. */
val C_CHAR: ValueLayout.OfByte = LINKER.canonicalLayouts()["char"] as ValueLayout.OfByte

/** C `int` type layout. */
val C_INT: ValueLayout.OfInt = LINKER.canonicalLayouts()["int"] as ValueLayout.OfInt

/** C `long` type layout. */
val C_LONG: ValueLayout.OfLong = LINKER.canonicalLayouts()["long"] as ValueLayout.OfLong

/** C `size_t` type layout. */
val C_SIZE_T: ValueLayout = LINKER.canonicalLayouts()["size_t"] as ValueLayout

/** C pointer type layout with unbounded target for string access. */
val C_POINTER: AddressLayout =
    (LINKER.canonicalLayouts()["void*"] as AddressLayout).withTargetLayout(
        MemoryLayout.sequenceLayout(Long.MAX_VALUE, C_CHAR)
    )

/** Windows wide character (UTF-16LE). */
val WCHAR: ValueLayout.OfChar = ValueLayout.JAVA_CHAR

/** Layout for capturing call state (errno). */
val CAPTURE_STATE_LAYOUT: StructLayout = Linker.Option.captureStateLayout()

/** VarHandle to access errno from captured state. */
val VH_ERRNO: VarHandle =
    CAPTURE_STATE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("errno"))

private val MH_STRERROR: MethodHandle by lazy {
  LINKER.downcallHandle(
      SYMBOL_LOOKUP.findOrThrow("strerror"),
      FunctionDescriptor.of(C_POINTER, C_INT),
  )
}

/** Returns the error message for the given [errno] value. */
fun strerror(errno: Int): String {
  val errMsg = MH_STRERROR.invokeExact(errno) as MemorySegment
  return errMsg.reinterpret(Long.MAX_VALUE).getString(0)
}

/** Extracts the errno value from a [capturedState] segment. */
fun errno(capturedState: MemorySegment): Int = VH_ERRNO.get(capturedState, 0L) as Int

/** Creates a downcall handle for the native [symbol] with the given [descriptor]. */
fun downcallHandle(
    symbol: String,
    descriptor: FunctionDescriptor,
    vararg options: Linker.Option,
): MethodHandle = LINKER.downcallHandle(SYMBOL_LOOKUP.findOrThrow(symbol), descriptor, *options)
