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
$ ./amper run -m app \
              --jvm-args="--enable-preview --add-modules=jdk.incubator.vector --enable-native-access=ALL-UNNAMED"
$ ./amper run -m macos --platform macosArm64

# JDK Incubator modules
$ ./amper run -m app \
              --jvm-args="--enable-preview --add-modules=jdk.incubator.vector --enable-native-access=ALL-UNNAMED" \
              --main-class=AppKt

# Dependency insights
$ ./amper show dependencies -m app --scope=runtime --filter=org.jetbrains.kotlin:kotlin-stdlib

# Check version updates in amper version catalog
$ brew install deezer/repo/caupain
$ caupain -i libs.versions.toml

# Publish to mavenLocal
$ ./amper publish mavenLocal

# List all the binaries
$ find . \( -path "*/build/*" -type f -perm +111 -o -path "*/build/*executableJar*/*.jar" \) | grep -v -E "(test|debug|dSYM)" | xargs du -h | sort -hr

$ find . \( -path "*/build/*" -perm +111 -o -path "*/build/tasks/*executableJar*/*.jar" \) -type f -ls | awk '{printf "%.3fM %s\n",$7/1048576,$NF}' | sort -rn

```

- Run on JVM

```bash
$ java --enable-preview \
       --add-modules=jdk.incubator.vector \
       --enable-native-access=ALL-UNNAMED \
       -jar build/tasks/_app_executableJarJvm/app-jvm-executable.jar
```

- Run on MacOS:

```bash
# Run MacOS Binary
$ build/tasks/_macos_linkMacosArm64Release/macos.kexe

# Show the logs
$ log stream \
  --info \
  --style syslog \
  --predicate 'senderImagePath ENDSWITH "macos.kexe"'

$ log show \
  --info \
  --style syslog \
  --predicate 'senderImagePath ENDSWITH "macos.kexe"' \
  --last 5m
  
# Run Win Binary
$ docker run --rm --platform="linux/amd64" \
             -e DISPLAY=host.docker.internal:0 \
             -v "$PWD":/work \
             scottyhardy/docker-wine:latest wine /work/build/tasks/_windows_linkMingwX64Release/windows.exe  
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
