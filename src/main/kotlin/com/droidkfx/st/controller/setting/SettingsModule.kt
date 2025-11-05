package com.droidkfx.st.controller.setting

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class SettingsModule {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val settingsDialog: SettingsDialog = SettingsDialog()
}