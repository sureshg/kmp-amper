package dev.suresh.lang

import kotlin.jvm.JvmInline

@JvmInline
value class Color(val argb: Int) {

  init {
    require(argb in 0..0xFFFFFF) { "Invalid color value: $argb" }
  }

  val red: Int
    get() = (argb shr 16) and 0xFF

  val green: Int
    get() = (argb shr 8) and 0xFF

  val blue: Int
    get() = argb and 0xFF

  fun toHexString() =
      argb.toHexString(
          HexFormat {
            upperCase = true
            number.prefix = "0x"
          }
      )
}
