package com.droidkfx.st.schwab.oauth.cert

import com.droidkfx.st.config.ConfigService
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.io.File
import java.security.SecureRandom

class CertificateService(
    private val configService: ConfigService,
    private val osTrustStore: OsTrustStore,
    private val keytool: CertificateKeytool,
) {
    private val logger = logger {}

    fun isCertificateReady(): Boolean {
        val cfg = configService.configDataBind.value.schwabConfig.callbackServerConfig
        return cfg.sslCertPassword.isNotEmpty()
                && cfg.sslCertAlias.isNotEmpty()
                && File(cfg.sslCertLocation).exists()
    }

    fun initializeIfNeeded() {
        if (isCertificateReady()) {
            logger.info { "Certificate already present, skipping initialization" }
            return
        }
        logger.info { "Certificate not ready — generating" }
        generateAndInstall()
    }

    fun reset() {
        logger.info { "Resetting certificate" }
        val cfg = configService.configDataBind.value.schwabConfig.callbackServerConfig
        val pfxFile = File(cfg.sslCertLocation)
        val cerFile = File(pfxFile.parent, "localhost.cer")

        if (pfxFile.exists()) pfxFile.delete()
        if (cerFile.exists()) cerFile.delete()

        osTrustStore.uninstall()
        generateAndInstall()
        logger.info { "Certificate reset complete" }
    }

    private fun generateAndInstall() {
        val cfg = configService.configDataBind.value.schwabConfig.callbackServerConfig
        val certPrivFile = File(cfg.sslCertLocation)
        val certFile = File(certPrivFile.parent, "localhost.cer")
        val password = generatePassword()
        val alias = configService.configDataBind.value.schwabConfig.callbackServerConfig.sslCertAlias

        certPrivFile.parentFile.mkdirs()

        keytool.generateKeyPair(certPrivFile, alias, password, cfg.sslCertType)
        logger.info { "${cfg.sslCertType} keystore generated at ${certPrivFile.absolutePath}" }

        keytool.exportCertificate(certPrivFile, certFile, alias, password, cfg.sslCertType)
        logger.info { "Certificate exported to ${certFile.absolutePath}" }

        osTrustStore.install(certFile)
        updateConfig(certPrivFile.absolutePath, password, alias)
    }

    private fun updateConfig(pfxPath: String, password: String, alias: String) {
        val current = configService.configDataBind.value
        configService.updateConfig(
            current.copy(
                schwabConfig = current.schwabConfig.copy(
                    callbackServerConfig = current.schwabConfig.callbackServerConfig.copy(
                        sslCertLocation = pfxPath,
                        sslCertPassword = password,
                        sslCertAlias = alias
                    )
                )
            )
        )
        logger.info { "Config updated with new certificate credentials" }
    }

    private fun generatePassword(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val random = SecureRandom()
        return (1..16).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }
}
