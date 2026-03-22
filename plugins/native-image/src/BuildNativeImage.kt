import org.jetbrains.amper.plugins.Classpath
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import org.jetbrains.amper.plugins.TaskAction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

@TaskAction
fun buildNativeImage(
    @Input classpath: Classpath,
    mainClass: String,
    additionalArgs: List<String> = emptyList(),
    @Output output: Path,
) {
    val javaHome = System.getenv("JAVA_HOME")
    requireNotNull(javaHome) { "JAVA_HOME environment variable is not set. Set it to a GraalVM distribution." }

    val nativeImageExe = Path.of(javaHome) / "bin" / nativeImageExecutableName()
    require(nativeImageExe.exists()) {
        "native-image executable not found at $nativeImageExe. Ensure JAVA_HOME points to a GraalVM distribution."
    }

    output.createParentDirectories()

    val command = buildList {
        add(nativeImageExe.toString())
        add("-cp")
        add(classpath.resolvedFiles.joinToString(File.pathSeparator))
        add(mainClass)
        add("-o")
        add(output.toString())
        add("--no-fallback")
        addAll(additionalArgs)
    }

    val process = ProcessBuilder(command)
        .inheritIO()
        .start()

    val exitCode = process.waitFor()
    require(exitCode == 0) { "native-image build failed with exit code $exitCode" }
}
