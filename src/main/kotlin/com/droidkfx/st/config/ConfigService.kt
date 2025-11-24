package com.droidkfx.st.config

import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ConfigService internal constructor(private val configRepository: ConfigRepository) {
    private val logger = logger {}

    val configDataBind = ValueDataBinding(configRepository.loadConfig())

    init {
        configDataBind.addListener { updateConfig(it) }
    }

    fun updateConfig(configEntity: ConfigEntity) {
        logger.trace { "updateConfig $configEntity" }
        configRepository.saveConfig(configEntity)
        configDataBind.value = configEntity
    }
}