package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.controller.account.AccountControllerModule
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.SchwabModule
import com.droidkfx.st.util.databind.toDataBinding
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking

class ControllerModule(
    schwabModule: SchwabModule,
    accountModule: AccountModule,
    positionModule: PositionModule,
    orderModule: OrderModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    private val accountData = runBlocking {
        positionModule.accountPositionService.getAccountPositions()
            .toMutableList()
            .toDataBinding()
    }

    private val accountControllerModule = AccountControllerModule(
        positionModule.accountPositionService,
        accountModule.accountService,
        accountData,
    )

    private val oauthService = schwabModule.oauthModule.oauthService
    val menuBarController = MenuBar(
        positionModule.accountPositionService,
        accountModule.accountService,
        oauthService,
        accountControllerModule.manageAccountDialog,
        accountData,
    )
    val statusBarController = StatusBar(oauthService)

    val accountTabs = AccountTabs(
        positionModule.accountPositionService,
        accountModule.accountService,
        accountData,
        oauthService.getTokenStatusBinding()
    ) {
        AccountTab(
            positionModule.accountPositionService,
            accountModule.accountService,
            orderModule.orderService,
            it
        )
    }
}
