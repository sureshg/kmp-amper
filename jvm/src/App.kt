import ffm.UnixSystem
import genai.voices

fun main() {
  println("User: ${UnixSystem.username}, uid: ${UnixSystem.uid}, gid: ${UnixSystem.gid}")
  vectorApi()
  voices()
}
