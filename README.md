## 🚀 Kotlin Multiplatform CLI

A Kotlin Multiplatform command-line application targeting macOS, Windows, Linux, and JVM platforms, built
with [Amper](https://github.com/JetBrains/amper) build tools. This project demonstrates a pleasant cross-platform
development experience with modern Kotlin tooling.

### Usage

```bash
# Update the amper
$ ./amper update --dev

# Build the app and create an executable jar
$ ./amper build [-v release]
$ ./amper package

# Run the tests
$ ./amper test

# Run the app
$ ./amper run --jvm-args=--enable-preview -m jvm
$ ./amper run -m macos --platform macosArm64

# JDK Incubator modules
$ ./amper run -m jvm --jvm-args="--enable-preview --add-modules=jdk.incubator.vector" --main-class=VectorKt

# Dependency insights
$ ./amper show dependencies -m jvm --scope=runtime --filter=org.jetbrains.kotlin:kotlin-stdlib

# Check version updates in amper version catalog
$ brew install deezer/repo/caupain
$ caupain -i libs.versions.toml

# Publish to mavenLocal
$ ./amper publish mavenLocal

# List all the binaries
$ find . \( -path "*/build/*" -type f -perm +111 -o -path "*/build/*executableJar*/*.jar" \) | grep -v -E "(test|debug|dSYM)" | xargs du -h | sort -hr
```

> [!TIP]
> You can find all the Amper CLI dev
> versions [here](https://packages.jetbrains.team/maven/p/amper/amper/org/jetbrains/amper/amper-cli/)

### Amper Config Settings

| Configuration     | 📝 Description                                                                                                                                    | 🎯 Applies To                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| `settings.kotlin` | Settings for the Kotlin compiler, thus only apply to Kotlin sources                                                                               | 🟣 Kotlin sources               |
| `settings.java`   | Settings for the Java compiler, thus only apply to Java sources                                                                                   | ☕ Java sources                  |
| `settings.jvm`    | Settings that apply to both Java and Kotlin sources (some common compiler options, settings related to the JDK in general, to the test JVM, etc.) | 🔄 Both Java and Kotlin sources |
