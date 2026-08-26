# CLDR — Agent guidance

## Build system

- **Maven, Java 21.** All tools are under `tools/`. Root `pom.xml` aggregates data only; the real build is `tools/pom.xml`.
- Four Maven modules in `tools/pom.xml`: `cldr-code`, `cldr-apps`, `cldr-rdf` (Java), plus `../docs/charts/keyboards` (npm).
- `cldr-apps-webdriver/` is a **standalone** Maven project (NOT a child of the parent POM). Uses Java 11 and JUnit 4.13.2 (not JUnit 5).
- **ICU4J** is pulled from `https://maven.pkg.github.com/unicode-org/icu` (version `79.0.1-20260318.053439-2`). GitHub token required for builds.
- **GITHUB_TOKEN required:** ICU4J lives on GitHub Packages, not Maven Central. Local builds need a `~/.m2/settings.xml` with server credentials for `githubicu` (or use the CI settings file at `.github/workflows/mvn-settings.xml`). Without this, `mvn compile` will fail with a 401/403.
- **Node.js v22** for frontend/keyboard tooling (see `.node-version`).

## Commands

```bash
# Build everything (skip tests)
mvn --file=tools/pom.xml -DskipTests=true compile install package

# Run all tests
mvn test --file=tools/pom.xml

# Run a single test class
mvn test --file=tools/pom.xml -pl cldr-code -Dtest=TestExample

# Run a single test method
mvn test --file=tools/pom.xml -pl cldr-code -Dtest=TestExample#testMethod

# Check formatting (Spotless / google-java-format AOSP style)
mvn --file=tools/pom.xml spotless:check

# Apply formatting
mvn --file=tools/pom.xml spotless:apply

# Run the CLDR data checker (requires prior build: mvn compile install)
java -jar tools/cldr-code/target/cldr-code.jar check -S common,seed -e -z BUILD

# Generate production data
mvn -DCLDR_DIR=$(pwd) --file=tools/pom.xml -pl cldr-code exec:java \
  -Dexec.mainClass=org.unicode.cldr.tool.GenerateProductionData \
  -Dexec.args="-d target/cldr-prod/common/"

# CLDRModify (auto-fixes locale data)
mvn -DCLDR_DIR=$(pwd) --file=tools/pom.xml -pl cldr-code exec:java \
  -Dexec.mainClass=org.unicode.cldr.tool.CLDRModify \
  -Dexec.args="-I -scommon/main"

# Run keyboard compilation
npm install -g @keymanapp/kmc && kmc --error-reporting build keyboards/3.0/*.xml

# Run JS tests (Survey Tool frontend)
cd tools/cldr-apps/js && npm ci && npm t

# Run JS tests with sandbox workaround (as CI does)
cd tools/cldr-apps/js && npm t -- -- -a no-sandbox -a disable-setuid-sandbox

# Run Survey Tool dev server
mvn --file=tools/pom.xml -DskipTests=true -pl cldr-apps liberty:dev
```

## Important conventions

- **Commit messages** must begin with a Jira ticket number: `CLDR-12345 Short description`.
- **CLDR_DIR** environment variable or system property (`-DCLDR_DIR=...`) must point to the repo root. Many tools and tests require it. Tests expect it via `-DCLDR_DIR=${project.basedir}/../../`.
- **Spotless** enforces google-java-format with AOSP style, `reflowLongStrings=false`.
- Use `ratchetFrom=NONE` — Spotless checks all files, not just changed ones.
- All PRs must reference an accepted Jira ticket and pass all CI checks.

## Testing

- Unit tests: JUnit 5 (`junit-jupiter:5.8.2`), Surefire with `-Xmx6g -enableassertions`.
- Integration tests: Failsafe, requires MySQL (`cldrdb` schema, `surveytool` user).
- WebDriver tests: standalone project under `tools/cldr-apps-webdriver/`, JUnit 4 + Selenium.
- Docker-based integration tests available via `tools/cldr-apps/docker-compose.yml`.
- A focused test command for unit tests in a single module:
  ```bash
  mvn test --file=tools/pom.xml -pl cldr-code -Dtest=TestClassName
  ```

## Repo structure

| Directory | Contents |
|-----------|----------|
| `common/` | Release locale data (main, supplemental, collation, transforms, DTDs, bcp47, etc.) |
| `seed/` | Draft/unstable locale data (same structure as `common/`) |
| `exemplars/` | Per-locale exemplar character sets (1542 XML files) |
| `keyboards/` | Keyboard layout definitions (reference + test), DTD, ABNF |
| `tools/cldr-code/` | Core Java library and CLI tools; main entrypoint `org.unicode.cldr.tool.Main` |
| `tools/cldr-apps/` | Survey Tool web application (Jakarta EE, WAR); Vue.js frontend at `js/src/` |
| `tools/cldr-rdf/` | RDF tooling (Apache Jena) |
| `tools/cldr-apps-webdriver/` | Standalone Selenium/WebDriver integration tests |
| `tools/scripts/` | Build scripts, ansible, tr-archive, keyboard ABNF tests, LLM tools |
| `docs/` | LDML spec source (TR35), Jekyll site for cldr.unicode.org, keyboard charts |

## Survey Tool (cldr-apps)

- Requires MySQL/MariaDB: schema `cldrdb` with charset `latin1` collation `latin1_bin`.
- Configure via `cldr.properties` (created on first startup; edit to remove `CLDR_MAINTENANCE=true`).
- Dev server: `mvn -DskipTests=true -pl cldr-apps liberty:dev` → http://localhost:9080/cldr-apps.
- Logging: `java.util.logging` via `SurveyLog.forClass()`.
- Frontend: Vue.js, built with webpack. JS tests at `tools/cldr-apps/js/`.
