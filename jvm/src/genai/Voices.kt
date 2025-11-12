package genai

import java.nio.file.Path
import org.pitest.voices.Chorus
import org.pitest.voices.ChorusConfig
import org.pitest.voices.alba.Alba
import org.pitest.voices.us.EnUsDictionary

fun main() {
  val chorusCfg = ChorusConfig(EnUsDictionary.en_us())
  val chorus = Chorus(chorusCfg)
  val voice = chorus.voice(Alba.albaMedium())
  val audio =
      voice.say(
          """
          Kotlin is a modern, statically typed programming language developed by JetBrains, 
          the company behind popular IDEs like IntelliJ IDEA. It is designed to run on the 
          Java Virtual Machine (JVM) and is fully interoperable with Java, allowing developers 
          to use Java libraries and frameworks seamlessly within Kotlin projects.
          """
              .trimIndent()
      )
  println(audio.toString())
  audio.save(Path.of("kotlin.mp3"))
  chorus.close()
}
