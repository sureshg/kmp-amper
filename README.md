## Amper Config Settings

| Configuration     | 📝 Description                                                                                                                                    | 🎯 Applies To                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| `settings.kotlin` | Settings for the Kotlin compiler, thus only apply to Kotlin sources                                                                               | 🟣 Kotlin sources               |
| `settings.java`   | Settings for the Java compiler, thus only apply to Java sources                                                                                   | ☕ Java sources                  |
| `settings.jvm`    | Settings that apply to both Java and Kotlin sources (some common compiler options, settings related to the JDK in general, to the test JVM, etc.) | 🔄 Both Java and Kotlin sources |

## Usage

```bash
$ ./amper update --dev
$ ./amper build -v release
$ ./amper package -v release
$ ./amper test
$ ./amper publish mavenLocal
$  find . \( -path "*/build/*" -type f -perm +111 -o -path "*/build/tasks/*executableJar*/*.jar" \) | grep -v -E "(test|debug|dSYM)" | xargs du -h | sort -hr
```