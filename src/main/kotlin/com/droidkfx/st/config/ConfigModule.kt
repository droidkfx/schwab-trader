package com.droidkfx.st.config

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ConfigModule() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val configRepository = ConfigRepository()
    val configService = ConfigService(configRepository)
}