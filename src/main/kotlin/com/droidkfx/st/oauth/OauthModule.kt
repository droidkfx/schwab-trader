package com.droidkfx.st.oauth

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.SchwabModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OauthModule(
    configModule: ConfigModule,
    schwabModule: SchwabModule,
    oauthToken: DataBinding<String?>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val oauthLocalServer = LocalServer(configModule.configService.getConfig().schwabConfig.callbackServerConfig)
    private val oauthRepository = OauthRepository()
    val oauthService = OauthService(oauthRepository, schwabModule.client.oathClient, oauthLocalServer, oauthToken)
}