# Schwab Trader

A desktop Swing application written in Kotlin for viewing brokerage accounts and positions, experimenting with
strategies, and placing orders using the Charles Schwab Trader API.

This project bundles a simple local HTTPS callback server to complete the Schwab OAuth flow, persists lightweight
configuration and data to a user directory, and provides a modular architecture around accounts, positions, orders,
transactions, and strategies.

Note: This project is not affiliated with Charles Schwab. Use at your own risk. Review the Schwab API terms of use and
paper-trade first.

## Features

- OAuth authentication against Schwab Trader API (with local HTTPS callback)
- Account discovery and caching
- Positions, allocations, and account tabs UI (Swing + FlatLaf)
- Basic strategy framework (e.g., Buy & Hold) and order submission plumbing
- Simple file-based repositories for configuration and cached data
- Modular design with clear separation of concerns

## Tech stack

- Kotlin 2.x (JVM, Java 21 toolchain)
- Swing UI with FlatLaf
- Ktor (HTTP client, lightweight Netty-based callback server)
- kotlinx.serialization (JSON)
- Kotlin Logging + Logback
- Gradle (Kotlin DSL), JUnit 5, JaCoCo

## Getting started

### Prerequisites

- Java 21 (JDK) installed and on PATH
- Git
- Charles Schwab Trader API application credentials (client key and secret)

### Clone

```
git clone https://github.com/droidkfx/schwab-trader.git
cd schwab-trader
```

### Build

Use the Gradle wrapper (recommended):

- Run unit tests
    - macOS/Linux: ./gradlew test
    - Windows: gradlew.bat test
- Build fat jar (includes dependencies)
    - macOS/Linux: ./gradlew jar
    - Windows: gradlew.bat jar

The resulting executable jar will be at:

- build/libs/schwab-trader-1.0-SNAPSHOT.jar (version may vary)

### Run

Since the jar manifest specifies the main class, you can run:

- java -jar build/libs/schwab-trader-1.0-SNAPSHOT.jar

### On Windows, a convenience task copies the jar to a fixed app directory:

- gradlew.bat releaseJarWin
    - Copies the built jar to %LOCALAPPDATA%\schwab-trader\app.jar and prints the destination

## Configuration

Configuration and data files are stored per-user.

- Windows: %APPDATA%\..\Local\schwab-trader
- macOS/Linux: ~/.schwab-trader

Key paths used by the app:

- Config repository root (default): <user_dir>/data
- OAuth callback certificate (default): <user_dir>/creds/localhost.pfx

These defaults are defined in code (see src/main/kotlin/com/droidkfx/st/config/ConfigEntity.kt and appDir.kt).

### Setting Schwab API credentials

You can set credentials in either of two ways:

1) Via the UI Settings dialog (recommended)

- Launch the app
- Open Settings
- Enter your Schwab API key and secret (and adjust callback server values if needed)

2) By editing the config file directly

- After first run (or manually), a JSON configuration is stored under the user app dir noted above
- Fields of interest:
    - schwabConfig.key
    - schwabConfig.secret
    - schwabConfig.callbackServerConfig (host, port, callbackPath, certificate details)

### OAuth callback server

The app spins up a local HTTPS server to receive the OAuth redirect from Schwab.

- Default host/port: 127.0.0.1:41241
- You must configure your Schwab API application with a redirect/callback URL that matches the app settings, e.g.:
    - https://127.0.0.1:41241/callback
- The local server uses a certificate at <user_dir>/creds/localhost.pfx (PKCS12) by default. You may point to another
  certificate or adjust password/alias in Settings.

## Flow overview:

1) Start the app, open Settings, ensure credentials and callback match your Schwab app registration
2) Trigger sign-in (OAuth) from within the app
3) Your browser opens the Schwab authorization page
4) After consent, Schwab redirects to your local HTTPS server
5) The app exchanges the code for tokens and stores them locally for subsequent calls

## Project layout

- src/main/kotlin/com/droidkfx/st/Main.kt — Application entry point (wires modules and starts UI)
- account — Account domain, repository and service
- position — Positions, targets, repository, and services
- orders — Orders service + client integration
- transaction — Transactions service + client integration
- schwab/client — Ktor clients for Accounts, Orders, Transactions, User Preferences
- schwab/oauth — LocalServer, OAuth models, repository, and service
- controller & view — Swing controllers/views and view models
- config — Config entities, repository, and helpers (per-user directories)
- util — Data binding, serialization helpers, file repository

## Testing and coverage

- Run tests:
    - macOS/Linux: ./gradlew test
    - Windows: gradlew.bat test
- Generate coverage report (JaCoCo):
    - macOS/Linux: ./gradlew jacocoTestReport
    - Windows: gradlew.bat jacocoTestReport
    - Reports are generated under build/reports/jacoco/test/html

## Logging

- Logging is configured via Logback
- Default configurations are provided in src/main/resources/logback.xml and src/test/resources/logback.xml

## Troubleshooting

- OAuth redirect fails
    - Ensure the callback URL in your Schwab API configuration exactly matches the app Settings (host, port, and path)
    - Confirm the local HTTPS server certificate path/password are correct
    - Firewall/AV may block local ports; allow 127.0.0.1:<port>
- API calls failing
    - Verify tokens are present and not expired; re-run sign-in
    - Double-check base API URL and credentials
- UI issues on Windows high-DPI
    - Try different FlatLaf themes or scale options

## Contributing

Pull requests are welcome. Please include tests where feasible and keep changes modular. Run tests and ensure Jacoco
reports generate cleanly.

## License

This project is provided as-is without warranty under an MIT License. See LICENSE for specifics.
