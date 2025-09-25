package com.droidkfx.st.config

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ConfigModule(location: String = "application.config.json") {
    private val logger = logger {}

    init {
        logger.trace { "Initializing from: $location" }
    }
    val configService = ConfigService(location)
}