package com.droidkfx.st.schwab.oauth.cert

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.io.File

interface OsTrustStore {
    fun install(cerFile: File)
    fun uninstall()

    companion object {
        fun forCurrentOs(): OsTrustStore {
            val os = System.getProperty("os.name").lowercase()
            return when {
                os.contains("win") -> WindowsOsTrustStore()
                os.contains("mac") -> MacOsTrustStore()
                else -> UnsupportedOsTrustStore()
            }
        }
    }
}

class WindowsOsTrustStore : OsTrustStore {
    override fun install(cerFile: File) =
        runOsTrustStoreProcess("certutil", "-user", "-addstore", "Root", cerFile.absolutePath)

    override fun uninstall() =
        runOsTrustStoreProcess("certutil", "-user", "-delstore", "Root", "localhost")
}

class MacOsTrustStore : OsTrustStore {
    override fun install(cerFile: File) = runOsTrustStoreProcess(
        "security", "add-trusted-cert",
        "-d", "-r", "trustRoot",
        "-k", "${System.getProperty("user.home")}/Library/Keychains/login.keychain-db",
        cerFile.absolutePath
    )

    override fun uninstall() =
        runOsTrustStoreProcess("security", "delete-certificate", "-c", "localhost")
}

class UnsupportedOsTrustStore : OsTrustStore {
    private val logger = logger {}

    override fun install(cerFile: File) = logger.warn {
        "Automatic trust store install is not supported on this platform. " +
                "Manually trust ${cerFile.absolutePath} in your browser."
    }

    override fun uninstall() = logger.warn {
        "Automatic trust store removal is not supported on this platform. " +
                "Manually remove the 'localhost' certificate from your browser trust store."
    }
}

private val osTrustStoreLogger = logger {}

private fun runOsTrustStoreProcess(vararg command: String) {
    osTrustStoreLogger.debug { "Running: ${command.joinToString(" ")}" }
    val process = ProcessBuilder(command.toList())
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()
    if (exitCode != 0) {
        osTrustStoreLogger.warn { "${command[0]} exited with code $exitCode: $output" }
    } else {
        osTrustStoreLogger.debug { "${command[0]} output: $output" }
    }
}