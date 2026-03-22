import org.jetbrains.amper.plugins.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

@TaskAction
fun runTracingAgent(
    @Input classpath: Classpath,
    mainClass: String,
    @Output resources: ModuleSources,
) {
    val javaHome = System.getenv("JAVA_HOME")
    requireNotNull(javaHome) { "JAVA_HOME environment variable is not set. Set it to a GraalVM distribution." }

    val javaExe = Path.of(javaHome) / "bin" / javaExecutableName()
    require(javaExe.exists()) {
        "java executable not found at $javaExe. Ensure JAVA_HOME points to a GraalVM distribution."
    }

    val outputDir = resources.sourceDirectories.first() / "META-INF/native-image/"
    outputDir.createDirectories()

    val command = buildList {
        add(javaExe.toString())
        add("-agentlib:native-image-agent=config-output-dir=$outputDir")
        add("-cp")
        add(classpath.resolvedFiles.joinToString(File.pathSeparator))
        add(mainClass)
    }

    val process = ProcessBuilder(command)
        .inheritIO()
        .start()

    val exitCode = process.waitFor()
    require(exitCode == 0) { "Tracing agent failed with exit code $exitCode" }
}
