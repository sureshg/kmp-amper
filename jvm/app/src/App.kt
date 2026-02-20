import ffm.*
import genai.voices
import io.roastedroot.lumis4j.core.*

suspend fun main() {
  System.setProperty("slf4j.internal.verbosity", "WARN")
  // select()
  println(PosixUser)
  mediaClient()
  vectorApi()
  syntaxHighlight()
  voices()
}

fun syntaxHighlight() {
  Lumis.builder().build().use {
    val h = it.highlighter().withLang(Lang.KOTLIN).withTheme(Theme.GITHUB_DARK).build()
    println(
        h.highlight(
                $$"""
                fun main(args: Array<String>) =
                    try {
                      initProps()
                      EngineMain.main(args)
                    } catch (e: Throwable) {
                      val log = KtorSimpleLogger("main")
                      log.error("Failed to start the app: ${e.message}", e)
                    }

                """
                    .trimIndent()
            )
            .string()
    )
  }
}
