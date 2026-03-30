# CLAUDE.md — Schwab Trader

Developer guide for Claude Code and AI-assisted development on this project.

## Build Commands

```bash
# Run all tests
./gradlew test                  # macOS/Linux
gradlew.bat test                # Windows

# Build fat JAR (all deps bundled)
./gradlew jar

# Full build (runs tests + JaCoCo coverage report)
./gradlew build

# Generate JaCoCo coverage report only
./gradlew jacocoTestReport
# Output: build/reports/jacoco/test/html/index.html

# Build Windows .exe via Launch4j
./gradlew createExe

# Build + copy .exe to %LOCALAPPDATA%\schwab-trader\
gradlew.bat releaseLocal
```

Run the fat JAR:

```bash
java -jar build/libs/schwab-trader-1.0-SNAPSHOT.jar
```

## Project Structure

```
src/main/kotlin/com/droidkfx/st/
├── Main.kt                  # Entry point — wires Koin modules, starts UI
├── account/                 # Account domain (model, service, repository, DI module)
├── position/                # Positions and allocation targets
├── orders/                  # Order placement and preview
├── transaction/             # Transaction history retrieval
├── strategy/                # Trading strategy interface + BuyHoldStrategy
├── schwab/
│   ├── client/              # Ktor HTTP clients for each Schwab API endpoint
│   └── oauth/               # OAuth 2.0 flow, local HTTPS callback server (Ktor/Netty)
├── config/                  # App configuration (persisted as JSON)
├── view/                    # Swing UI (JFrame, tabs, table, menus, status bar)
│   ├── model/               # ViewModels (MVVM pattern)
│   └── setting/             # Settings dialog
└── util/
    ├── databind/            # Custom observable ValueDataBinding / ListDataBinding
    ├── progress/            # ProgressService for async task tracking
    ├── repository/          # FileRepository base for JSON persistence
    └── serialization/       # Custom KBigDecimal and Instant serializers
```

## Architecture Patterns

**Dependency Injection:** Koin 4.x. Every domain has a `*Module.kt` file declaring its Koin bindings. All modules are
wired in `Main.kt`. Never bypass Koin to instantiate services directly in production code.

**MVVM:** Swing views hold a reference to their ViewModel. ViewModels hold `ValueDataBinding<T>` / `ListDataBinding<T>`
observables. Views register listeners on these bindings to update the UI reactively — do not mutate UI state from a
ViewModel directly.

**Repository pattern:** `FileRepository` is the base class for JSON persistence to the user's app directory.
Repositories deal only with serialization/deserialization; services own business logic.

**Coroutines:** All Schwab API calls are `suspend` functions. UI-dispatched coroutines use `kotlinx.coroutines-swing`.
Do not block the Swing EDT with I/O.

**Observable Bindings:** `ValueDataBinding<T>` (single value) and `ListDataBinding<T>` (list). Use `.readOnly()` to
expose bindings from a ViewModel without allowing external mutation. Use `.mapped()` to derive a new binding from an
existing one.

## Key Dependency Versions

| Library               | Version                                   |
|-----------------------|-------------------------------------------|
| Kotlin                | 2.2.0                                     |
| kotlinx.serialization | 1.9.0                                     |
| kotlinx.coroutines    | 1.10.2                                    |
| Ktor                  | 3.4.0 (gradle.properties `ktor_version`)  |
| Koin                  | 4.1.1                                     |
| FlatLaf               | 3.6.1                                     |
| JUnit 5               | 5.8.1 (gradle.properties `junit_version`) |
| MockK                 | 1.14.0                                    |
| Logback               | 1.5.25                                    |

**Important:** Netty is force-pinned to `4.2.11.Final` in `build.gradle.kts` to address a transitive CVE from
`ktor-server-netty`. Do not remove that resolution strategy without verifying Ktor has resolved the vulnerability in a
newer release.

## Testing Conventions

- **JUnit 5 + MockK** — use `@Test` from `kotlin.test` or `org.junit.jupiter.api`.
- **Fixture files** live alongside tests (e.g., `AccountFixture.kt`, `PositionFixtures.kt`). Add shared test data there
  rather than inlining it in individual tests.
- **MockK** is used to mock Schwab API clients in service tests. All client methods are `suspend`; use `coEvery` /
  `coVerify`.
- **Repository tests** use a temp directory. See existing `PositionRepositoryTest` / `OauthRepositoryTest` for the
  pattern.
- Tests mirror the source package structure under `src/test/kotlin/com/droidkfx/st/`.

## Runtime Configuration

User config and data is stored outside the project directory:

| Platform    | Path                            |
|-------------|---------------------------------|
| Windows     | `%LOCALAPPDATA%\schwab-trader\` |
| Linux/macOS | `~/.schwab-trader/`             |

Subdirectories:

- `data/` — JSON files for accounts, positions, targets, OAuth tokens
- `creds/` — PKCS12 certificate for the local OAuth callback server (`localhost.pfx` by default)

Config is loaded from `config.json` in that directory. On first launch it is created with defaults. The Settings dialog
writes back to this file.

## OAuth Flow

1. User opens **Settings**, enters Schwab API key/secret, configures callback URL.
2. User triggers **Update OAuth** from the menu.
3. `OauthService.runInitialAuthorization()` starts a Ktor/Netty HTTPS server on `127.0.0.1:41241`.
4. The user's browser opens to the Schwab authorization page.
5. After consent, Schwab redirects to the local server; the code is exchanged for tokens.
6. Tokens (access + refresh) are persisted by `OauthRepository`.
7. `BaseClient` auto-refreshes the access token when expired; triggers a full re-auth if the refresh token is also
   expired.

The Schwab app registration **must** have the redirect URI matching the callback server settings (host, port, path).

## Logging

- Development: `src/main/resources/logback.xml` — console, TRACE level.
- Release exe: `logback-release.xml` (set via `-Dlogback.configurationFile=logback-release.xml` in Launch4j config).
- Noisy Netty/Ktor/SSL loggers are suppressed in both configs.
- Uses `kotlin-logging-jvm` (`KotlinLogging.logger {}`). Do not use `println` for diagnostic output in new code.

## Windows Executable

`./gradlew createExe` produces `build/launch4j/schwab-trader.exe` via the Launch4j plugin.

- Header type: `gui` (no console window in release).
- JVM option: `-Dlogback.configurationFile=logback-release.xml`.
- Icon: `src/main/resources/AppIcon.ico`.
- `releaseLocal` copies the exe to `%LOCALAPPDATA%\schwab-trader\`.

## Common Pitfalls

- **EDT threading:** All Swing mutations must happen on the Event Dispatch Thread. Use `SwingUtilities.invokeLater` or
  the Swing coroutine dispatcher when bridging from a coroutine.
- **BigDecimal serialization:** `KBigDecimal` is a custom `@Serializable` wrapper. Use it (not raw `BigDecimal`)
  whenever you need to serialize monetary or quantity values to JSON.
- **Order of Koin modules:** The module list in `Main.kt` is load-order sensitive. `configModule` must come before
  `schwabModule`; `schwabModule` before the domain modules that depend on API clients.
- **Tests vs. production config:** Tests use their own temp directories. Never hardcode the user app dir path — always
  go through `appDir.kt`.