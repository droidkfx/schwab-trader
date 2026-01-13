package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.context.GlobalContext

class SchwabModule() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val configService: ConfigService by GlobalContext.get().inject()

    val oauthTokenBinding = ValueDataBinding<String?>(null)
    val oauthTokenStatus = OauthStatus.NOT_INITIALIZED.toDataBinding()
    val tokenRefreshSignal = false.toDataBinding()

    val clientModule = SchwabClientModule(
        configService.configDataBind,
        oauthTokenBinding,
        oauthTokenStatus,
        tokenRefreshSignal,
    )
    val oauthModule = OauthModule(
        configService.configDataBind,
        clientModule.oathClient,
        oauthTokenStatus,
        oauthTokenBinding,
        tokenRefreshSignal
    )
}