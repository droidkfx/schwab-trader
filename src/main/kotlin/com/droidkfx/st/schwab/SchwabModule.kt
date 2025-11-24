package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SchwabModule(configModule: ConfigModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val oauthTokenBinding = ValueDataBinding<String?>(null)
    val oauthTokenStatus = OauthStatus.NOT_INITIALIZED.toDataBinding()
    val tokenRefreshSignal = false.toDataBinding()

    val clientModule = SchwabClientModule(
        configModule.configService.configDataBind,
        oauthTokenBinding,
        oauthTokenStatus,
        tokenRefreshSignal,
    )
    val oauthModule = OauthModule(
        configModule.configService.configDataBind,
        clientModule.oathClient,
        oauthTokenStatus,
        oauthTokenBinding,
        tokenRefreshSignal
    )
}