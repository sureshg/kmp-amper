import dev.suresh.http.json
import dev.suresh.rpc.MyService
import io.github.oshai.kotlinlogging.*
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.util.logging.KtorSimpleLogger
import kotlin.io.path.*
import kotlinx.rpc.krpc.ktor.server.*
import kotlinx.rpc.krpc.serialization.json.json
import rpc.*

fun main(args: Array<String>) =
    try {
      initProps()
      EngineMain.main(args)
    } catch (e: Throwable) {
      val log = KtorSimpleLogger("main")
      log.error("Failed to start the app: ${e.message}", e)
    }

suspend fun Application.module() {
  log.info("Starting the app ...")
  install(Krpc)

  routing {
    get("/") { call.respondText("Kotlin ${KotlinVersion.CURRENT} - Amper!") }

    rpc("/rpc") {
      rpcConfig { serialization { json(json) } }
      registerService<MyService> { MyServiceImpl(MyServiceParam("Amper")) }
    }

    swaggerUI(path = "docs") {
      info =
          OpenApiInfo(
              title = "Ktor App",
              version = "1.0.0",
          )
    }
  }
}

/**
 * Initializes the system properties required for the application to run. This should be invoked
 * before the Engine main() method is called.
 */
fun initProps() {
  val logDir =
      System.getProperty("LOG_DIR", System.getenv("LOG_DIR")).orEmpty().ifBlank {
        when {
          Path("/log").exists() -> "/log"
          else -> System.getProperty("user.dir")
        }
      }

  System.setProperty("jdk.tls.maxCertificateChainLength", "15")
  System.setProperty("jdk.includeInExceptions", "hostInfo")
  System.setProperty("slf4j.internal.verbosity", "WARN")
  System.setProperty("LOG_DIR", logDir)
  KotlinLoggingConfiguration.logStartupMessage = false

  println("⚡ Application started ⚡")
  println("Log Dir: $logDir")
}
