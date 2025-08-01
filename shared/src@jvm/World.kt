actual class World {
  actual fun get(): String {
    println(ScopedValue.newInstance<String>())
    return "JVM World ${JVersion.get()}"
  }
}
