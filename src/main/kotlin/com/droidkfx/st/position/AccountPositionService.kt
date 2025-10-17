package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountPositionService(private val accountService: AccountService, private val positionService: PositionService) {
    private val logger = logger {}

    fun getAccountPositions(): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return getAccountPositions(accountService.listAccounts())
    }

    fun getAccountPositions(accounts: List<Account>): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return accounts.map {
            AccountPosition(it, positionService.getAccountPosition(it.id))
        }
    }
}