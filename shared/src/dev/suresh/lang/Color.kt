package dev.suresh.lang

import kotlin.jvm.JvmInline

@JvmInline
value class Color(val argb: Int) {

  init {
    require(argb in 0..0xFFFFFFFF) { "Invalid color value: $argb" }
  }

  /** Returns the alpha component in the range 0-255 */
  val alpha: Int
    get() = (argb shr 24) and 0xFF

  /** Returns the red component in the range 0-255 in the default sRGB space. */
  val red: Int
    get() = (argb shr 16) and 0xFF

  /** Returns the green component in the range 0-255 in the default sRGB space. */
  val green: Int
    get() = (argb shr 8) and 0xFF

  /** Returns the blue component in the range 0-255 in the default sRGB space. */
  val blue: Int
    get() = (argb shr 0) and 0xFF

  fun toHexString() =
      argb.toHexString(
          HexFormat {
            upperCase = true
            number.prefix = "0x"
          }
      )
}
