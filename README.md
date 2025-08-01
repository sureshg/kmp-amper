```bash
$ ./amper update --dev
$ ./amper build -v release
$ ./amper package -v release
$ ./amper test
$ ./amper publish mavenLocal
$  find . \( -path "*/build/*" -type f -perm +111 -o -path "*/build/tasks/*executableJar*/*.jar" \) | grep -v -E "(test|debug|dSYM)" | xargs du -h | sort -hr
```