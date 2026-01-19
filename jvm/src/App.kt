import ffm.*
import genai.voices

suspend fun main() {
  System.setProperty("slf4j.internal.verbosity", "WARN")
  println(PosixUser)
  mediaClient()
  vectorApi()
  voices()
}
