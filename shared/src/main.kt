import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Person(val name: String, val age: Int)

fun main() {
    println("Hello Kotlin ${KotlinVersion.CURRENT} - ${World().get()}")
    val s = Json.decodeFromString<Person>(
        """
                {"name": "John", "age": 30}
              """.trimIndent()
    )
    println(s)
}
