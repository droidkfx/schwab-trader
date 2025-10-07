package com.droidkfx.st.position

import com.droidkfx.st.account.Service
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountPositionService(private val accountService: Service, private val positionService: PositionService) {
    private val logger = logger {}

    fun getAccountPositions(): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return accountService.listAccounts().map {
            AccountPosition(it, positionService.getAccountPosition(it.id))
        }
    }
}