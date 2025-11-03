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

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    val configModule = ConfigModule("application.no-commit.config.json")

    val schwabModule = SchwabModule(configModule)
    val accountModule = AccountModule(configModule, schwabModule)
    val strategyModule = StrategyModule()
    val transactionModule = TransactionModule()
    val positionModule = PositionModule(configModule, accountModule, schwabModule.clientModule, strategyModule)
    val orderModule = OrderModule(schwabModule.clientModule)
    val controllerModule = ControllerModule(
        schwabModule,
        accountModule,
        positionModule,
        orderModule
    )

    controllerModule.mainController.showAndRun()
}