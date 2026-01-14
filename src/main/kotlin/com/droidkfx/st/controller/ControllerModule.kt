package com.droidkfx.st.controller

import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.position.PositionModule
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

class ControllerModule(
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
        GlobalContext.get().get(),
        GlobalContext.get().get(),
        accountData,
    )
    val statusBarController = StatusBar(GlobalContext.get().get())

    val accountTabs = AccountTabs(
        positionModule.accountPositionService,
        GlobalContext.get().get(),
        accountData,
        GlobalContext.get().get<OauthService>().getTokenStatusBinding()
    ) {
        AccountTab(
            positionModule.accountPositionService,
            GlobalContext.get().get(),
            orderModule.orderService,
            it
        )
    }
}
