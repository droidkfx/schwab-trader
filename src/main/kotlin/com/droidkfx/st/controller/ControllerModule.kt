package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.controller.account.AccountControllerModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.oauth.OauthModule
import com.droidkfx.st.util.databind.toDataBinding
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ControllerModule(oathModule: OauthModule, accountModule: AccountModule, positionModule: PositionModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    private val accountData = positionModule.accountPositionService.getAccountPositions()
        .toMutableList()
        .toDataBinding()

    private val accountControllerModule = AccountControllerModule(
        positionModule.accountPositionService,
        accountModule.accountService,
        accountData,
    )

    private val menuBarController = MenuBar(
        accountModule.accountService,
        oathModule.oauthService,
        accountControllerModule.manageAccountDialog,
        accountData,
    )
    private val statusBarController = StatusBar(oathModule.oauthService)

    private val accountTabs = AccountTabs(
        positionModule.accountPositionService,
        accountModule.accountService,
        accountData,
        oathModule.oauthService.getTokenStatusBinding()
    )

    val mainController = Main(
        statusBarController = statusBarController,
        menuBarController = menuBarController,
        accountTabs = accountTabs,
    )
}
