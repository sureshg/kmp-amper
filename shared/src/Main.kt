import dev.suresh.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking { mediaClient() }

suspend fun mediaClient() {
  println("Kotlin ${KotlinVersion.CURRENT} - ${Platform().name()}")
  val client = MediaApiClient()
  println(client)
  val images = client.images()
  println("Images: ${images.size}")
  val videos = client.videos()
  println("Videos: ${videos.size}")
}
