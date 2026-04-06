# Schwab Trader

A desktop Swing application written in Kotlin for viewing brokerage accounts and positions, experimenting with
strategies, and placing orders using the Charles Schwab Trader API.

The app bundles a local HTTPS callback server to complete the Schwab OAuth 2.0 flow, persists lightweight configuration
and data to a per-user directory, and provides a modular architecture built around accounts, positions, orders,
transactions, and strategies.

> **Disclaimer:** This project is not affiliated with Charles Schwab. Use at your own risk. Review the Schwab API terms
> of use and paper-trade before placing real orders.

---

## Features

- **OAuth 2.0 authentication** against the Schwab Trader API, using a local Ktor/Netty HTTPS callback server
- **Account discovery and caching** — linked brokerage accounts are fetched, hashed, and stored locally
- **Positions and allocation targets** — view current holdings alongside your desired allocation percentages
- **Buy & Hold rebalancing strategy** — calculates buy/hold recommendations to bring positions in line with targets
  using available cash; distributes whole shares proportionally, with leftover cash allocated to cheapest underweight
  positions
- **Order placement and preview** — submit or preview orders against the Schwab API
- **Transaction history** — retrieve recent activity for reconciliation
- **File-based persistence** — configuration, accounts, positions, and tokens are stored as JSON in a per-user directory
- **Reactive Swing UI** — custom observable data-binding drives UI updates without a heavy framework
- **Modular Koin DI architecture** with clear separation between domain, service, repository, and UI layers

---

## Tech Stack

| Layer                      | Library / Tool                          |
|----------------------------|-----------------------------------------|
| Language                   | Kotlin 2.2.0 (JVM, Java 21 toolchain)   |
| UI                         | Swing + FlatLaf 3.6.1                   |
| HTTP client / OAuth server | Ktor 3.4.0 (Java client + Netty server) |
| Serialization              | kotlinx.serialization 1.9.0             |
| Coroutines                 | kotlinx.coroutines-swing 1.10.2         |
| Dependency Injection       | Koin 4.1.1                              |
| Logging                    | kotlin-logging-jvm + Logback 1.5.25     |
| Build                      | Gradle 8+ (Kotlin DSL), Launch4j 4.0.0  |
| Testing                    | JUnit 5, MockK 1.14.0, JaCoCo           |

---

## Getting Started

### Prerequisites

- Java 21 JDK installed and on `PATH`
- Git
- A [Charles Schwab Trader API](https://developer.schwab.com) application with a client key, secret, and redirect URI
  configured

### Clone

```bash
git clone https://github.com/droidkfx/schwab-trader.git
cd schwab-trader
```

### Build

```bash
# Run unit tests
./gradlew test              # macOS/Linux
gradlew.bat test            # Windows

# Build fat JAR (all dependencies bundled)
./gradlew jar

# Full build — runs tests and generates JaCoCo coverage report
./gradlew build
```

The fat JAR is written to:

```
build/libs/schwab-trader-1.0-SNAPSHOT.jar
```

### Run

```bash
java -jar build/libs/schwab-trader-1.0-SNAPSHOT.jar
```

### Windows — release to local app directory

```bash
gradlew.bat releaseLocal
```

Builds a native Windows executable (via Launch4j) and copies it to:

```
%LOCALAPPDATA%\schwab-trader\schwab-trader.exe
```

---

## Configuration

### User data directory

All configuration and cached data are stored in a per-user directory outside the project:

| Platform      | Path                            |
|---------------|---------------------------------|
| Windows       | `%LOCALAPPDATA%\schwab-trader\` |
| macOS / Linux | `~/.schwab-trader/`             |

Key subdirectories:

| Path     | Contents                                                                 |
|----------|--------------------------------------------------------------------------|
| `data/`  | JSON repositories — accounts, positions, targets, OAuth tokens           |
| `creds/` | PKCS12 certificate for the local OAuth callback server (`localhost.pfx`) |

On first launch, `config.json` is created in this directory with default values.

### Setting Schwab API credentials

**Recommended — via the UI Settings dialog:**

1. Launch the app
2. Open **Settings** from the menu bar
3. Enter your Schwab API **key** and **secret**
4. Adjust the callback server host, port, path, and certificate settings to match your Schwab app registration

**Alternative — edit `config.json` directly:**

```json
{
  "schwabConfig": {
    "key": "YOUR_CLIENT_KEY",
    "secret": "YOUR_CLIENT_SECRET",
    "baseApiUrl": "api.schwabapi.com",
    "callbackServerConfig": {
      "host": "127.0.0.1",
      "port": 41241,
      "callbackPath": "",
      "sslCertLocation": "/path/to/creds/localhost.pfx",
      "sslCertPassword": "...",
      "sslCertAlias": "...",
      "sslCertType": "PKCS12"
    }
  }
}
```

### OAuth callback server

The app runs a local HTTPS server to receive the redirect from Schwab after the user grants consent.

- Default address: `https://127.0.0.1:41241`
- Your Schwab API application's **redirect URI** must exactly match the configured host, port, and path — for example:
  `https://127.0.0.1:41241/callback`
- The server requires a PKCS12 certificate. On first launch the app generates one automatically (see below).

### SSL certificate — automatic management

On first launch, `CertificateService` detects that no certificate is present and automatically:

1. Generates a self-signed PKCS12 keystore at `<user_dir>/creds/localhost.pfx` using the JVM-bundled `keytool`
   (RSA 2048, 10-year validity, Subject Alternative Names for `localhost` and `127.0.0.1`).
2. Exports the public certificate to `<user_dir>/creds/localhost.cer`.
3. Installs the certificate to the OS user trust store so the browser accepts it without warnings:
    - **Windows** — `certutil -user -addstore Root` (Windows will show a one-time security confirmation dialog)
    - **macOS** — `security add-trusted-cert` targeting `login.keychain-db`
    - **Linux** — not automated; the log message prints the `.cer` path for manual import
4. Saves the generated path, password, and alias back to `config.json`.

**Resetting the certificate:** Open the menu bar and choose **Reset Certificate**. The existing certificate is
deleted and uninstalled from the OS trust store, then the above process repeats. A confirmation dialog is shown
before any changes are made.

---

## OAuth Flow

1. Open **Settings**, enter credentials, confirm the callback URL matches your Schwab app registration.
2. Select **Update OAuth** from the menu.
3. The local HTTPS server starts; your browser opens to the Schwab authorization page.
4. After consent, Schwab redirects to the local server; the app exchanges the code for access and refresh tokens.
5. Tokens are stored locally. The access token is refreshed automatically when it expires; a full re-auth is triggered
   if the refresh token is also expired.

---

## Project Layout

```
src/main/kotlin/com/droidkfx/st/
├── Main.kt              # Entry point — initializes Koin, starts Swing UI
├── account/             # Account domain: model, service, repository, DI module
├── position/            # Positions and allocation targets
├── orders/              # Order placement and preview
├── transaction/         # Transaction history
├── strategy/            # Strategy interface + BuyHoldStrategy
├── schwab/
│   ├── client/          # Ktor HTTP clients (Accounts, Orders, Transactions, Quotes, OAuth)
│   └── oauth/           # OAuth service, local HTTPS callback server, token repository
│       └── cert/        # Certificate lifecycle (CertificateService, CertificateKeytool, OsTrustStore)
├── config/              # App configuration entity, service, repository, per-user paths
├── view/                # Swing UI (JFrame, tabs, allocation table, menus, status bar)
│   ├── model/           # ViewModels (MVVM pattern, reactive via ValueDataBinding)
│   └── setting/         # Settings dialog
└── util/
    ├── databind/        # ValueDataBinding / ListDataBinding — observable property system
    ├── progress/        # ProgressService for tracking async operation status
    ├── repository/      # FileRepository base class for JSON persistence
    └── serialization/   # Custom KBigDecimal and Instant serializers
```

---

## Testing and Coverage

```bash
# Run all unit tests
./gradlew test

# Generate JaCoCo HTML coverage report
./gradlew jacocoTestReport
# Output: build/reports/jacoco/test/html/index.html
```

Test areas include:

- Repository CRUD (account, position, target, OAuth token)
- OAuth service flow (token exchange, refresh, re-auth)
- `BuyHoldStrategy` allocation algorithm (25+ cases)
- Schwab API model serialization/deserialization
- ViewModels and observable data binding
- `ProgressService` async task tracking
- Custom `KBigDecimal` and `Instant` serializers

---

## Logging

- **Development:** `src/main/resources/logback.xml` — console output, TRACE level
- **Release exe:** `logback-release.xml`, activated via JVM option `-Dlogback.configurationFile=logback-release.xml` (
  set in the Launch4j config)
- Verbose Netty, Ktor, and SSL loggers are suppressed in both configurations

---

## Troubleshooting

**OAuth redirect fails**

- Confirm the redirect URI in your Schwab API application exactly matches the host, port, and path in Settings
- Check that the PKCS12 certificate path, password, and alias are correct in Settings (these are set automatically on
  first launch; use **Reset Certificate** from the menu if they appear missing or corrupt)
- Firewall or antivirus may block local ports; allow `127.0.0.1:<port>`

**Browser shows a certificate warning on the OAuth callback page**

- The self-signed certificate may not have been installed to the OS trust store. Try **Reset Certificate** from the
  menu to regenerate and reinstall it
- On Windows, confirm you clicked **Yes** on the security dialog that appeared during certificate installation
- On Linux, manually import `<user_dir>/creds/localhost.cer` into your browser's certificate store

**API calls failing**

- Verify the access token is not expired; re-run OAuth if needed
- Double-check the base API URL (`api.schwabapi.com`) and credentials

**UI issues on Windows high-DPI displays**

- Try adjusting FlatLaf theme options or system DPI scaling settings

---

## Contributing

Pull requests are welcome. Please:

- Include tests for new functionality
- Keep changes focused and modular
- Run `./gradlew build` (tests + coverage) before submitting
- Follow existing patterns — Koin DI modules, FileRepository for persistence, ViewModels for UI state

---

## License

This project is provided as-is without warranty under the MIT License. See `LICENSE` for details.