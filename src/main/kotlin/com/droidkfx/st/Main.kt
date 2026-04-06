package com.droidkfx.st

import com.droidkfx.st.account.accountModule
import com.droidkfx.st.config.configModule
import com.droidkfx.st.orders.orderModule
import com.droidkfx.st.position.positionModule
import com.droidkfx.st.schwab.oauth.cert.CertificateService
import com.droidkfx.st.schwab.schwabModule
import com.droidkfx.st.strategy.strategyModule
import com.droidkfx.st.transaction.transactionModule
import com.droidkfx.st.util.KoinLogger
import com.droidkfx.st.view.Main
import com.droidkfx.st.view.viewModule
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.context.startKoin
import java.time.Instant
import javax.swing.JOptionPane

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    logger.info { "user.home: ${System.getProperty("user.home")}" }
    logger.info { "java home: ${System.getProperty("java.home")}" }
    logger.info { "java version: ${System.getProperty("java.version")}" }
    logger.info { "java vendor: ${System.getProperty("java.vendor")}" }
    logger.info { "java vm name: ${System.getProperty("java.vm.name")}" }
    logger.info { "java vm version: ${System.getProperty("java.vm.version")}" }
    logger.info { "java vm vendor: ${System.getProperty("java.vm.vendor")}" }

    val startTime = Instant.now()
    val app = startKoin {
        logger(KoinLogger())
        modules(
            configModule,
            schwabModule,
            accountModule,
            strategyModule,
            orderModule,
            transactionModule,
            positionModule,
            viewModule
        )
    }
    try {
        app.koin.get<CertificateService>().initializeIfNeeded()
    } catch (e: Exception) {
        logger.error(e) { "Certificate initialization failed" }
        JOptionPane.showMessageDialog(
            null,
            "Certificate initialization failed: ${e.message}\n" +
                    "You may manually configure a certificate in Settings.",
            "Certificate Error",
            JOptionPane.ERROR_MESSAGE
        )
    }

    app.koin.get<Main>().showAndRun()

    val initFinishTime = Instant.now()
    logger.info { "Startup complete in ${initFinishTime.toEpochMilli() - startTime.toEpochMilli()}ms" }
}