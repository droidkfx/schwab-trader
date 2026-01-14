package com.droidkfx.st

import com.droidkfx.st.account.accountModule
import com.droidkfx.st.config.configModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.orders.orderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.schwabModule
import com.droidkfx.st.strategy.strategyModule
import com.droidkfx.st.transaction.TransactionModule
import com.droidkfx.st.util.KoinLogger
import com.droidkfx.st.view.ViewModule
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.context.startKoin
import java.time.Instant

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    val startTime = Instant.now()
    startKoin {
        logger(KoinLogger())
        modules(
            configModule,
            schwabModule,
            accountModule,
            strategyModule,
            orderModule
        )
    }

    val transactionModule = TransactionModule()
    val positionModule = PositionModule(
        transactionModule
    )
    val controllerModule = ControllerModule(
        positionModule,
    )

    ViewModule(controllerModule).main.showAndRun()

    val initFinishTime = Instant.now()
    logger.info { "Startup complete in ${initFinishTime.toEpochMilli() - startTime.toEpochMilli()}ms" }
}