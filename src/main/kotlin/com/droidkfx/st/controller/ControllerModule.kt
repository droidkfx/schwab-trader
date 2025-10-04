package com.droidkfx.st.controller

import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.view.model.AccountTabViewModel
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ControllerModule(oathModule: OauthModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    private val manageAccountsController = ManageAccounts()
    private val menuBarController = MenuBar(oathModule.oauthService, manageAccountsController)
    private val statusBarController = StatusBar(oathModule.oauthService)

    val accounts: DataBinding<List<AccountTabViewModel>?> = DataBinding(listOf())

    private val accountTabs = AccountTabs(accounts)
    private val mainController = Main(
        statusBarController = statusBarController,
        menuBarController = menuBarController,
        accountTabs = accountTabs,
    )
}