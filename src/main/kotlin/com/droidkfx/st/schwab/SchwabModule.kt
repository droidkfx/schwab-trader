package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.client.SchwabClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SchwabModule(configModule: ConfigModule, oauthToken: DataBinding<String?>) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }
    val client = SchwabClient(
        configModule.configService.getConfig().schwabConfig,
        oauthToken
    )
}