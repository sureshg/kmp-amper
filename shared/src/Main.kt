import dev.suresh.*

fun main() {
  println("Kotlin ${KotlinVersion.CURRENT} - ${Platform().name()}")
}

suspend fun mediaClient() {
  println("Kotlin ${KotlinVersion.CURRENT} - ${Platform().name()}")
  val client = MediaApiClient()
  println(client)
  val images = client.images()
  println("Images: ${images.size}")
  val videos = client.videos()
  println("Videos: ${videos.size}")
}
