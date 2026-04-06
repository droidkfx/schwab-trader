package com.droidkfx.st.schwab.oauth.cert

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.io.File

interface CertificateKeytool {
    fun generateKeyPair(certPrivFile: File, alias: String, password: String, storeType: String)
    fun exportCertificate(certPrivFile: File, certFile: File, alias: String, password: String, storeType: String)

    companion object {
        fun jdk(): CertificateKeytool = JdkKeyTool()
    }
}

private class JdkKeyTool : CertificateKeytool {
    private val logger = logger {}

    override fun generateKeyPair(certPrivFile: File, alias: String, password: String, storeType: String) = run(
        "-genkeypair",
        "-alias", alias,
        "-keyalg", "RSA",
        "-keysize", "2048",
        "-validity", "3650",
        "-keystore", certPrivFile.absolutePath,
        "-storetype", storeType,
        "-storepass", password,
        "-keypass", password,
        "-dname", "CN=localhost, O=schwab-trader, C=US",
        "-ext", "SAN=dns:localhost,ip:127.0.0.1"
    )

    override fun exportCertificate(
        certPrivFile: File,
        certFile: File,
        alias: String,
        password: String,
        storeType: String
    ) = run(
        "-exportcert",
        "-alias", alias,
        "-keystore", certPrivFile.absolutePath,
        "-storetype", storeType,
        "-storepass", password,
        "-file", certFile.absolutePath,
        "-rfc"
    )

    private fun run(vararg args: String) {
        val keytool = "${System.getProperty("java.home")}/bin/keytool"
        val command = listOf(keytool) + args.toList()
        logger.debug { "Running keytool: ${command.joinToString(" ")}" }
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            logger.error { "keytool failed (exit $exitCode): $output" }
            throw RuntimeException("keytool failed with exit code $exitCode: $output")
        }
        logger.debug { "keytool output: $output" }
    }
}
