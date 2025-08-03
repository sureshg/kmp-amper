import dev.suresh.*
import kotlinx.coroutines.*

fun main() = runBlocking {
  println("Kotlin ${KotlinVersion.CURRENT} - ${Platform().name()}")
  val client = MediaApiClient()
  val images = client.images()
  println("Images: ${images.size}")
  val videos = client.videos()
  println("Videos: ${videos.size}")
}
