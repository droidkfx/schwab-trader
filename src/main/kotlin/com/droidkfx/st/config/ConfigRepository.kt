package com.droidkfx.st.config

import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class ConfigRepository() :
    FileRepository(
        logger {},
        getUsersAppDirPath()
    ) {

    private var currentConfig: ConfigEntity? = null
    private val configFileName = "config"

    fun loadConfig(): ConfigEntity {
        logger.trace { "loadConfig" }
        if (currentConfig == null) {
            return load(configFileName) ?: ConfigEntity().also { saveConfig(it) }
        } else {
            return currentConfig!!
        }
    }

    fun saveConfig(configEntity: ConfigEntity) {
        logger.trace { "saveConfig $configEntity" }
        currentConfig = configEntity
        save(configFileName, configEntity)
    }
}