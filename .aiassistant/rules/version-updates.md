---
apply: always
---

# Update Version Catalog

## Task

Run `caupain --no-cache -i libs.versions.toml` to find the latest versions, then update the catalog.

## Steps

1. Run `caupain --no-cache -i libs.versions.toml` in the project root
2. Review the reported updates
3. Update `libs.versions.toml` with the new versions
4. Update `module.yaml` if it has inline versions (e.g., Kotlin, JUnit, JVM release)
5. No other changes to the codebase

## Rules

1. Always use `caupain` to find the latest versions — don't manually check Maven Central or GitHub
2. Only update version values — never change keys, formatting, spacing, alignment, or line order
3. Prefer stable releases over pre-release/beta unless the project already uses a pre-release version
4. Never modify Amper build config (`module.yaml`) beyond version bumps
5. The version catalog is at the project root (`libs.versions.toml`), not under `gradle/`

## Example

```toml
# BEFORE
kotlinx-io = "0.8.2"

# AFTER
kotlinx-io = "0.9.0"
```
