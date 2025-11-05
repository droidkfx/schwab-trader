package com.droidkfx.st.config

import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ConfigService internal constructor(private val configRepository: ConfigRepository) {
    private val logger = logger {}

    fun getConfig(): ValueDataBinding<ConfigEntity> {
        logger.trace { "getConfig" }
        val valueDataBinding = ValueDataBinding(configRepository.loadConfig())
        valueDataBinding.addListener { updateConfig(it) }
        return valueDataBinding
    }

    fun updateConfig(configEntity: ConfigEntity) {
        logger.trace { "updateConfig $configEntity" }
        configRepository.saveConfig(configEntity)
    }
}