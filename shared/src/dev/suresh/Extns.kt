package dev.suresh

import kotlin.contracts.contract

/**
 * Retrieves a command-line argument value by its flag.
 *
 * Return type is inferred from [T]: `getArg<Int>` throws if missing, `getArg<Int?>` returns null if
 * missing.
 */
inline fun <reified T> Array<String>.getArg(
    flag: String,
    default: T? = null,
    noinline transform: ((String) -> T?)? = null,
): T =
    indexOf(flag)
        .takeIf { it >= 0 }
        ?.let { getOrNull(it + 1) }
        ?.let { transform?.invoke(it) ?: it as? T }
        ?: default
        ?: nullOrThrow("Missing required argument: $flag")

/**
 * Returns null if [T] is nullable, otherwise throws with [message].
 *
 * Uses reified magic: `null is T` checks if T is nullable, `null as T` safely casts when it is.
 */
inline fun <reified T> nullOrThrow(message: String): T =
    when {
      null is T -> null as T
      else -> error(message)
    }

/**
 * Runs [block] on this value if [condition] is true, otherwise returns this unchanged.
 *
 * Example: `"hello".runIf(shouldCapitalize) { uppercase() }`
 */
inline fun <T : R, R> T.runIf(condition: Boolean, block: T.() -> R): R {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    condition.holdsIn(block)
  }
  return if (condition) block() else this
}
