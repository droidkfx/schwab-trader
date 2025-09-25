package com.droidkfx.st

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.oauth.OauthModule
import com.droidkfx.st.schwab.SchwabModule
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting Schwab Trader" }
    val configModule = ConfigModule("application.no-commit.config.json")
    val oauthTokenBinding = DataBinding<String?>(null)
    val schwabModule = SchwabModule(configModule, oauthTokenBinding)
    val oauthModule = OauthModule(configModule, schwabModule, oauthTokenBinding)

    schwabModule.client.accountsClient.listAccountNumbers()

    ControllerModule(oauthModule)
}