package com.droidkfx.st

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.SchwabModule
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    val configModule = ConfigModule("application.no-commit.config.json")

    val schwabModule = SchwabModule(configModule)
    val accountModule = AccountModule()
    val positionModule = PositionModule(accountModule)
    val controllerModule = ControllerModule(schwabModule.oauthModule, positionModule)

    controllerModule.mainController.showAndRun()
}