package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.util.databind.DataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SchwabModule(configModule: ConfigModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val oauthTokenBinding = DataBinding<String?>(null)
    val clientModule = SchwabClientModule(
        configModule.configService.getConfig().schwabConfig,
        oauthTokenBinding
    )
    val oauthModule = OauthModule(
        configModule.configService.getConfig(),
        clientModule.oathClient,
        oauthTokenBinding
    )
}