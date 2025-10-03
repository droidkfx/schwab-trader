package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.CallbackServerConfig
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.client.OauthClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OauthModule(
    serverConfig: CallbackServerConfig,
    oauthClient: OauthClient,
    oauthToken: DataBinding<String?>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val oauthLocalServer = LocalServer(serverConfig)
    private val oauthRepository = OauthRepository()
    val oauthService = OauthService(oauthRepository, oauthClient, oauthLocalServer, oauthToken)
}