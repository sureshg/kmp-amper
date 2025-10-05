import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.compose.reload.DevelopmentEntryPoint

@Composable
@DevelopmentEntryPoint
fun App() {
  var count by remember { mutableStateOf(0) }
  MaterialTheme {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Button(onClick = { count++ }) { BasicText(text = "Click me!") }
      BasicText(
          text = "Click count: $count",
      )
    }
  }
}

fun main() = singleWindowApplication(title = "Compose Desktop App") { App() }
