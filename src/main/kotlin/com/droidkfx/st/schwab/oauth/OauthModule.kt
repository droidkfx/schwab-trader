package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.client.OauthClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OauthModule(
    config: ConfigEntity,
    oauthClient: OauthClient,
    oauthToken: DataBinding<String?>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val oauthLocalServer = LocalServer(config.schwabConfig.callbackServerConfig)
    private val oauthRepository = OauthRepository(config)
    val oauthService = OauthService(oauthRepository, oauthClient, oauthLocalServer, oauthToken)
}