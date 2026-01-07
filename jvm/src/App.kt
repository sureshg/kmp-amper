import ffm.*
import genai.voices

suspend fun main() {
  System.setProperty("slf4j.internal.verbosity", "WARN")
  println("Posix User: ${PosixUser.username}, uid: ${PosixUser.uid}, gid: ${PosixUser.gid}")
  mediaClient()
  vectorApi()
  voices()
}
