import dev.suresh.http.json
import dev.suresh.rpc.MyService
import io.ktor.server.application.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import rpc.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
  install(Krpc)
  routing {
    get("/") { call.respondText("Kotlin ${KotlinVersion.CURRENT} - Amper!") }
    rpc("/rpc") {
      rpcConfig { serialization { json(json) } }
      registerService<MyService> { MyServiceImpl(MyServiceParam("Amper")) }
    }
  }
}
