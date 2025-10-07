package com.droidkfx.st.position

import com.droidkfx.st.account.AccountModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionModule(accountModule: AccountModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val positionService = PositionService()
    val accountPositionService = AccountPositionService(accountModule.accountService, positionService)
}