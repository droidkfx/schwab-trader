package com.droidkfx.st.view

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.view.setting.SettingsModule
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ViewModule(
    configModule: ConfigModule,
    controllerModule: ControllerModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    private val settingsModule = SettingsModule(configModule)

    val main = Main(
        StatusBar(controllerModule.statusBarController),
        MenuBar(controllerModule.menuBarController, settingsModule.settingsDialog),
        AccountTabs(controllerModule.accountTabs)
    )
}