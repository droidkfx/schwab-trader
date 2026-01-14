package com.droidkfx.st.controller

import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

class ControllerModule(
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val accountData = runBlocking {
        GlobalContext.get().get<AccountPositionService>().getAccountPositions()
            .toMutableList()
            .toDataBinding()
    }

    val menuBarController = MenuBar(
        GlobalContext.get().get(),
        GlobalContext.get().get(),
        GlobalContext.get().get(),
        accountData,
    )
    val statusBarController = StatusBar(GlobalContext.get().get())

    val accountTabs = AccountTabs(
        GlobalContext.get().get(),
        GlobalContext.get().get(),
        accountData,
        GlobalContext.get().get<OauthService>().getTokenStatusBinding()
    ) {
        AccountTab(
            GlobalContext.get().get(),
            GlobalContext.get().get(),
            GlobalContext.get().get(),
            it
        )
    }
}
