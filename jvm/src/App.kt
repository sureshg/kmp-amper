import ffm.*
import genai.voices

fun main() {
  println("Posix User: ${PosixUser.username}, uid: ${PosixUser.uid}, gid: ${PosixUser.gid}")
  vectorApi()
  voices()
}
