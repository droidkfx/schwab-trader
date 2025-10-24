package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SchwabModule(configModule: ConfigModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val oauthTokenBinding = ValueDataBinding<String?>(null)
    val tokenRefreshSignal = ValueDataBinding(false)
    private lateinit var requestRefresh: () -> Unit
    val clientModule = SchwabClientModule(
        configModule.configService.getConfig().schwabConfig,
        oauthTokenBinding,
        tokenRefreshSignal
    )
    val oauthModule = OauthModule(
        configModule.configService.getConfig(),
        clientModule.oathClient,
        oauthTokenBinding,
        tokenRefreshSignal
    )
}