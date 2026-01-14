package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

class ControllerModule(
    accountModule: AccountModule,
    positionModule: PositionModule,
    orderModule: OrderModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val accountData = runBlocking {
        positionModule.accountPositionService.getAccountPositions()
            .toMutableList()
            .toDataBinding()
    }

    val menuBarController = MenuBar(
        positionModule.accountPositionService,
        accountModule.accountService,
        GlobalContext.get().get(),
        accountData,
    )
    val statusBarController = StatusBar(GlobalContext.get().get())

    val accountTabs = AccountTabs(
        positionModule.accountPositionService,
        accountModule.accountService,
        accountData,
        GlobalContext.get().get<OauthService>().getTokenStatusBinding()
    ) {
        AccountTab(
            positionModule.accountPositionService,
            accountModule.accountService,
            orderModule.orderService,
            it
        )
    }
}
