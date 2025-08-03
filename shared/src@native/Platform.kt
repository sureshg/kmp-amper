import kotlin.native.Platform

actual class Platform {
  actual fun name() = "Kotlin Native ${Platform.osFamily.name}"
}
