package com.droidkfx.st.controller.setting

import com.droidkfx.st.config.ConfigModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SettingsModule(configModule: ConfigModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val settingsDialog: SettingsDialog = SettingsDialog(configModule.configService.configDataBind)
}