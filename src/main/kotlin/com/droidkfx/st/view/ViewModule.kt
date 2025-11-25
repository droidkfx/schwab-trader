package com.droidkfx.st.view

import com.droidkfx.st.controller.ControllerModule
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ViewModule(
    controllerModule: ControllerModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    val main = Main(
        StatusBar(controllerModule.statusBarController),
        MenuBar(controllerModule.menuBarController),
        AccountTabs(controllerModule.accountTabs)
    )
}