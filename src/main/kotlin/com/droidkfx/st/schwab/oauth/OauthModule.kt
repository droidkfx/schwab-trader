package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OauthModule(
    config: ValueDataBinding<ConfigEntity>,
    oauthClient: OauthClient,
    oauthTokenStatus: ValueDataBinding<OauthStatus>,
    oauthToken: ValueDataBinding<String?>,
    tokenRefreshSignal: ValueDataBinding<Boolean>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val oauthLocalServer = LocalServer(config.value.schwabConfig.callbackServerConfig)
    private val oauthRepository = OauthRepository(config.readOnlyMapped { it.repositoryRoot })
    val oauthService =
        OauthService(oauthRepository, oauthClient, oauthLocalServer, oauthTokenStatus, oauthToken, tokenRefreshSignal)
}