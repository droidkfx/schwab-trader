package com.droidkfx.st.controller

import com.droidkfx.st.oauth.OauthModule
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ControllerModule(oathModule: OauthModule) {
    private val logger = logger {}
    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    val menuBarController = MenuBar(oathModule.oauthService)
    val statusBarController = StatusBar(oathModule.oauthService)
    val accountTabs = AccountTabs()
    val mainController = Main(
        statusBarController = statusBarController,
        menuBarController = menuBarController,
        accountTabs = accountTabs,
    )
}