package com.droidkfx.st.config

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ConfigService internal constructor(private val configRepository: ConfigRepository) {
    private val logger = logger {}
    private var config: ConfigEntity? = null

    fun getConfig(): ConfigEntity {
        return configRepository.loadConfig()
    }
}