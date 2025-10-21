package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OauthModule(
    config: ConfigEntity,
    oauthClient: OauthClient,
    oauthToken: ValueDataBinding<String?>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val oauthLocalServer = LocalServer(config.schwabConfig.callbackServerConfig)
    private val oauthRepository = OauthRepository(config)
    val oauthService = OauthService(oauthRepository, oauthClient, oauthLocalServer, oauthToken)
}