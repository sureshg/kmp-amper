import io.ktor.server.application.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
  routing { get("/") { call.respondText("Kotlin ${KotlinVersion.CURRENT} - Amper!") } }
}
