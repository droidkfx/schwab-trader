package com.droidkfx.st

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.configModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.SchwabModule
import com.droidkfx.st.strategy.StrategyModule
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
        )
    }

    val schwabModule = SchwabModule()
    val accountModule = AccountModule(schwabModule)
    val strategyModule = StrategyModule(schwabModule.clientModule)
    val orderModule = OrderModule(schwabModule.clientModule)
    val transactionModule = TransactionModule(schwabModule.clientModule)
    val positionModule = PositionModule(
        accountModule,
        schwabModule.clientModule,
        strategyModule,
        orderModule,
        transactionModule
    )
    val controllerModule = ControllerModule(
        schwabModule,
        accountModule,
        positionModule,
        orderModule
    )

    ViewModule(controllerModule).main.showAndRun()

    val initFinishTime = Instant.now()
    logger.info { "Startup complete in ${initFinishTime.toEpochMilli() - startTime.toEpochMilli()}ms" }
}