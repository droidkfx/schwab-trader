package com.droidkfx.st

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.SchwabModule
import com.droidkfx.st.strategy.StrategyModule
import com.droidkfx.st.transaction.TransactionModule
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Instant

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    val startTime = Instant.now()
    val configModule = ConfigModule()

    val schwabModule = SchwabModule(configModule)
    val accountModule = AccountModule(configModule, schwabModule)
    val strategyModule = StrategyModule(schwabModule.clientModule)
    val orderModule = OrderModule(schwabModule.clientModule)
    val transactionModule = TransactionModule(schwabModule.clientModule)
    val positionModule = PositionModule(
        configModule,
        accountModule,
        schwabModule.clientModule,
        strategyModule,
        orderModule,
        transactionModule
    )
    val controllerModule = ControllerModule(
        configModule,
        schwabModule,
        accountModule,
        positionModule,
        orderModule
    )

    controllerModule.mainController.showAndRun()
    val initFinishTime = Instant.now()
    logger.info { "Startup complete in ${initFinishTime.toEpochMilli() - startTime.toEpochMilli()}ms" }
}